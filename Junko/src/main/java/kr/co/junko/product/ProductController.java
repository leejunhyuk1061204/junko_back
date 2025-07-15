package kr.co.junko.product;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.category.CategoryService;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.ProductDTO;
import kr.co.junko.util.Jwt;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class ProductController {

	@Autowired ProductService service;
	@Autowired CategoryService categoryService;
	
	Map<String, Object> result = null;
	
	@Value("${spring.servlet.multipart.location}")
	private String uploadDir;
	
	// 상품 등록
	@PostMapping(value="/product/insert")
	public Map<String, Object> productInsert(
			@ModelAttribute ProductDTO dto,
			@RequestHeader Map<String, String> header) {
		
		log.info("dto : {}", dto);
		result = new HashMap<String,Object>();

		// 1. 토큰에서 사용자 정보 추출
		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		String loginId = (String) payload.get("user_id");

		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;

		if (login) {
			// 2. 이미지 null 방지
			MultipartFile[] files = dto.getImages();
			if (files == null) {
				files = new MultipartFile[] {};
			}

			// 3. 상품 등록 실행
			success = service.productInsert(files, dto);
		}

		result.put("success", success);
		result.put("loginYN", login);
		result.put("idx", dto.getProduct_idx());
		
		return result;
	}

	
	// 상품 CSV 파일 등록
	@PostMapping(value="/product/csv")
	public Map<String, Object> productCsvInsert(
		@RequestParam("file") MultipartFile file,
		@RequestHeader Map<String, String> header) {
		
		log.info("CSV file : {}", file);
		result = new HashMap<String, Object>();

		// 1. 토큰에서 사용자 정보 추출
		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		String loginId = (String) payload.get("user_id");

		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;

		if (login) {
			// CSV 파일 등록
			success = service.productCsvInsert(file);
		}

		result.put("success", success);
		result.put("loginYN", login);
		
		return result;
	}

	// 상품 이미지만 등록
	@PostMapping("/product/{product_idx}/imgInsert")
	public Map<String, Object> uploadProductImg(
		@PathVariable("product_idx") int product_idx,
		@RequestParam("images") MultipartFile[] files,
		@RequestHeader Map<String, String> header) {
		result = new HashMap<String,Object>();

		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		String loginId = (String) payload.get("user_id");

		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;

		if (login) {
			success = service.productImgInsert(product_idx, files);
		}

		result.put("success", success);
		result.put("loginYN", login);

		return result;
	}

	// 상품 수정
	@PutMapping("/product/update")
	public Map<String, Object> updateProduct(
		@ModelAttribute ProductDTO dto,
		@RequestHeader Map<String, String> header) {
		log.info("dto: {}", dto);
		result = new HashMap<String,Object>();

		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		String loginId = (String) payload.get("user_id");

		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;

		if (login) {
			success = service.productUpdate(dto, loginId);
		}
		
		result.put("success", success);
		result.put("loginYN", login);
		return result;
	}

	// 상품 이미지 수정
	// 기존 이미지 소프트 삭제 후 새 이미지 등록
	@PutMapping("/product/{product_idx}/imgUpdate")
	public Map<String, Object> updateProductImg(
		@PathVariable("product_idx") int product_idx,
		@RequestParam(value = "images", required = false) MultipartFile[] files,
		@RequestParam(value = "remainImageUrls", required = false) List<String> remainImageUrls,
		@RequestHeader Map<String, String> header) {

		result = new HashMap<String,Object>();

		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		String loginId = (String) payload.get("user_id");

		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;

		if (login) {
			success = service.updateProductImg(product_idx, files, remainImageUrls);
		}

		result.put("success", success);
		result.put("loginYN", login);

		return result;
	}
	// 상품 이미지 소프트 삭제
	@PutMapping("/product/{product_idx}/imgDel")
	public Map<String, Object> softDelProductImg(
		@PathVariable("product_idx") int product_idx,
		@RequestHeader Map<String, String> header) {

		result = new HashMap<String,Object>();

		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		String loginId = (String) payload.get("user_id");

		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;

		if (login) {
			success = service.softDelProductImg(product_idx);
		}
		result.put("success", success);
		result.put("loginYN", login);

		return result;
	}

	// 상품 자체 소프트 삭제
	@PutMapping("/product/{product_idx}/del")
	public Map<String, Object> softDelProduct(
		@PathVariable("product_idx") int product_idx,
		@RequestHeader Map<String, String> header) {

		result = new HashMap<String,Object>();

		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		String loginId = (String) payload.get("user_id");

		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;

		if (login) {
			success = service.softDelProduct(product_idx);
		}
		
		result.put("success", success);
		result.put("loginYN", login);
		return result;
	}

	// 상품 목록
	@PostMapping("/product/list")
	public Map<String, Object> getProductList(@RequestBody Map<String, Object> param) {
	    int category_idx = (int) param.getOrDefault("category_idx", 0);
	    String search = (String) param.getOrDefault("search", "");
	    String sort = (String) param.getOrDefault("sort", "latest");
	    int page = (int) param.getOrDefault("page", 1);
	    int size = (int) param.getOrDefault("size", 10);

	    // ✅ 카테고리 + 하위 포함 리스트 구하기
	    List<Integer> categoryList = categoryService.getCategoryWithChildren(category_idx);
	    int start = (page - 1) * size;

	    param.put("categoryList", categoryList);
	    param.put("start", start);
	    param.put("size", size);
	    param.put("search", search.trim());
	    param.put("sort", sort);

	    Map<String, Object> result = new HashMap<>();
	    result.put("list", service.getProductList(param));
	    result.put("total", service.getProductCateIdxTotal(param));
	    result.put("success", true);
	    return result;
	}

	
	// 문서 업로드
	@PostMapping("/product/{product_idx}/docs")
	public Map<String, Object> productDocsUpload(
	    @PathVariable("product_idx") int product_idx,
	    @RequestParam("docs") MultipartFile[] files,
	    @RequestHeader Map<String, String> header) {
	    result = new HashMap<String,Object>();
	    
	    String token = header.get("authorization");
	    Map<String, Object> payload = Jwt.readToken(token);
	    String loginId = (String) payload.get("user_id");

	    boolean login = loginId != null && !loginId.isEmpty();
	    boolean success = false;

	    if (login) {
	        success = service.productDocsUpload(product_idx, files);
	    }

	    result.put("success", success);
	    result.put("loginYN", login);
	    return result;
	}
	
	// 문서 삭제
	@PutMapping("/product/docs/{doc_id}/del")
	public Map<String, Object> productDocsDel(
	    @PathVariable("doc_id") int doc_id,
	    @RequestHeader Map<String, String> header) {
		
	    result = new HashMap<String,Object>();
	    String token = header.get("authorization");
	    Map<String, Object> payload = Jwt.readToken(token);
	    String loginId = (String) payload.get("user_id");

	    boolean login = loginId != null && !loginId.isEmpty();
	    boolean success = false;

	    if (login) {
	        success = service.productDocsDel(doc_id);
	    }

	    result.put("success", success);
	    result.put("loginYN", login);
	    return result;
	}
	
	// 문서 리스트
	@GetMapping("/product/{product_idx}/docs")
	public Map<String, Object> productDocsList(@PathVariable("product_idx") int product_idx) {
		result = new HashMap<String,Object>();
	    List<FileDTO> docs = service.productDocsList(product_idx);
	    result.put("docs", docs);
	    return result;
	}

	// 문서 미리보기 or 다운로드 - Authorization 없이 가능하게
	@GetMapping("/product/docs/{fileName}")
	public ResponseEntity<Resource> publicProductDoc(
	    @PathVariable("fileName") String fileName) {

	    try {
	        // ori_filename 조회
	        String oriFilename = service.downloadProductDoc(fileName);
	        if (oriFilename == null) return ResponseEntity.notFound().build();

	        // 실제 파일 로드
	        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
	        Resource resource = new UrlResource(filePath.toUri());
	        if (!resource.exists()) return ResponseEntity.notFound().build();

	        // 파일 content type 설정
	        String contentType = Files.probeContentType(filePath);
	        if (contentType == null) contentType = "application/octet-stream";

	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType(contentType))
	                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + oriFilename + "\"")
	                .body(resource);

	    } catch (Exception e) {
	        log.error("문서 미리보기 실패: {}", fileName, e);
	        return ResponseEntity.internalServerError().build();
	    }
	}


	// 상품 상세보기
	@GetMapping("/product/detail/{product_idx}")
	public Map<String, Object> productDetail(@PathVariable int product_idx) {
		Map<String, Object> result = new HashMap<String, Object>();
		ProductDTO dto = service.productDetail(product_idx);
		result.put("data", dto);
		result.put("success", dto != null);
		return result;
	}

	// 상품 + 옵션 + 컴바인드
	@PostMapping(value="/productNoption/list")
	public Map<String, Object> productNoptionList(@RequestBody Map<String, Object>param){
		log.info("param : {}",param);
		return service.productNoptionList(param);
	}
	
}