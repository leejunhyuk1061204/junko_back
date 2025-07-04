package kr.co.junko.product;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
	
	public boolean productInsert(MultipartFile[] files, ProductDTO dto) {
		// 상품 저장
		int row = dao.productInsert(dto);
		if (row == 0) return false;
		int idx = dto.getProduct_idx();
		boolean save_success = files.length > 0 ? fileSave(files, idx) : true;
		
		return save_success;
	}

	private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
	
	private boolean fileSave(MultipartFile[] files, int idx) {
		boolean success = false;
		String ori_filename = "";
		String new_filename = "";
		String ext = "";
		String type = "product";

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
					success = true;
				} catch (Exception e) {
					log.error("파일 저장 실패: {}", ori_filename, e);
					success = false;
				}
			}
		}
		return success;
	}
}