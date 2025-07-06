package kr.co.junko.product;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService {
	
	@Autowired ProductDAO dao;
	
	@Value("${spring.servlet.multipart.location}")	
	private String root;
	
	Map<String, Object> result = null;
	
	@Transactional
	public boolean productInsert(MultipartFile[] files, ProductDTO dto) {
		// 상품 저장
		int row = dao.productInsert(dto);
		if (row == 0) return false;
		int idx = dto.getProduct_idx();
		boolean save_success = files.length > 0 ? fileSave(files, idx) : true;
		
		if (!save_success) {
			throw new RuntimeException("파일 저장 실패로 트랜잭션 롤백");
		}

		return save_success;
	}

	// 허용된 이미지 확장자 목록
	// .jpg, .jpeg, .png 만 허용
	private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png");
	
	private boolean fileSave(MultipartFile[] files, int idx) {
		boolean success = false;
		String ori_filename = "";
		String new_filename = "";
		String ext = "";
		String type = "product";

		if (files == null || files.length == 0) return true;

		for (MultipartFile file : files) {
			// 이름 추출
			ori_filename = file.getOriginalFilename();
			if (!file.isEmpty()) {
				ext = ori_filename.substring(ori_filename.lastIndexOf("."));
				
				// 확장자 제한
				if (!ALLOWED_EXTENSIONS.contains(ext)) {
					log.warn("허용되지 않은 파일 확장자: {}", ext);
					continue;
				}
				
				// 이름 변경
				new_filename = UUID.randomUUID().toString() + ext;
				Path path = Paths.get(root + "/" + new_filename);

				// 변경된 이름으로 파일 저장
				try {
					byte[] arr = file.getBytes();
					Files.write(path, arr);
					dao.fileWrite(ori_filename, new_filename, idx, type);
				} catch (Exception e) {
					throw new RuntimeException("파일 저장 또는 DB등록 실패: " + ori_filename, e);
				}
			}
		}
		return true;
	}

	// 엑셀로 csv 파일 등록하니까 자동 "" 붙는 문제 발생
	// 메모장에 내용 복사 후 UTF-8 .csv 파일로 저장 후 업로드하면 정상 작동
	public boolean productCsvInsert(MultipartFile file) {
		// CSV 파일 등록
		// CSV는 관리자가 상품을 대량으로 등록하기 위한 입력 도구이므로, 서버에 별도 저장하지 않는다.
		// 업로드된 CSV 파일은 메모리에서 바로 파싱 후 처리되며, 별도 파일 저장은 필요 없다.
		if (file.isEmpty()){return false;}
		try {

			// CSV 파일을 읽기 위한 BufferedReader 생성
			BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));

			// CSV 파일 읽기
			String line;
			int successCount = 0;

			line = reader.readLine(); // 헤더 라인 건너뛰기 (실제 데이터가 있는 줄만 읽어오기)
			
			while ((line = reader.readLine()) != null) { // 실제 데이터가 null 이 아니면
				String[] values = line.split(","); // 쉼표로 분리
				// CSV 컬럼 순서대로 매핑
				ProductDTO dto = new ProductDTO();
				dto.setProduct_name(values[0].trim()); // trim : 공백 제거
				dto.setPurchase_price(Integer.parseInt(values[1].trim()));
				dto.setSelling_price(Integer.parseInt(values[2].trim()));
				dto.setDiscount_rate(Integer.parseInt(values[3].trim()));
				dto.setProduct_standard(values[4].trim());
				dto.setCategory_idx(Integer.parseInt(values[5].trim()));

				// DB 삽입
				int row = dao.productInsert(dto);
				if (row > 0) {
					successCount++;
				}
			}

			log.info("CSV 상품 등록 성공 수 : {}", successCount);
			return successCount > 0;
		} catch (Exception e) {
			log.error("CSV 파일 처리 중 오류 발생", e);
			return false;
		}
	}

	// 상품 이미지 등록
	public boolean productImgInsert(int productIdx, MultipartFile[] files) {
		if (files == null || files.length == 0) return false;
		return fileSave(files, productIdx); // 기존 fileSave 재활용
	}

	// 상품 수정
	// 상품 정보 수정은 이미지 수정과 분리되어 있음
	public boolean productUpdate(ProductDTO dto) {
		int row = dao.productUpdate(dto);
		return row > 0;
	}

	// 상품 이미지 소프트 삭제
	public boolean softDelProductImg(int product_idx) {
		int row = dao.softDelProductImg(product_idx);
		return row > 0;
	}

	// 상품 이미지 수정
	@Transactional
	public boolean updateProductImg(int product_idx, MultipartFile[] files) {
		dao.softDelProductImg(product_idx); 

		// 새 이미지 없으면 여기서 종료 (기존 이미지 숨김 처리만 하고 끝)
		if (files == null || files.length == 0) return true;

		// 새 이미지가 있으면 저장
		return fileSave(files, product_idx);
	}

	// 상품 소프트 삭제
	public boolean softDelProduct(int product_idx) {
		int row = dao.softDelProduct(product_idx);
		return row > 0;
	}

	// 상품 목록 조회
	public Map<String, Object> productList(Map<String, Object> param) {
		int page = (int) param.getOrDefault("page", 1);
    	int size = (int) param.getOrDefault("size", 10);
    	String search = (String) param.getOrDefault("search", "");
    	int category = (int) param.getOrDefault("category", 0);
    	String sort = (String) param.getOrDefault("sort", "product_idx");

		int start = (page - 1) * size;

		List<ProductDTO> list = dao.productList(start, size, search, category, sort); // 상품 목록 조회
		int total = dao.productTotalCnt(search, category); // 전체 상품 개수 조회

		Map<String, Object> result = new HashMap<>();

		result.put("list", list);
		result.put("total", total);
		result.put("page", page);

		return result;
	}
}