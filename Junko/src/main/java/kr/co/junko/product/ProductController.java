package kr.co.junko.product;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.ProductDTO;
import kr.co.junko.util.Jwt;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class ProductController {

	@Autowired ProductService service;
	
	Map<String, Object> result = null;
	
	// 상품 등록
	@PostMapping(value="/product/insert")
	public Map<String, Object> productInsert(
			@ModelAttribute ProductDTO dto,
			@RequestHeader Map<String, String> header) {
		
		log.info("dto : {}", dto);
		Map<String, Object> result = new HashMap<>();

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
		@RequestHeader Map<String, String> header
	) {
		Map<String, Object> result = new HashMap<>();

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
		Map<String, Object> result = new HashMap<>();

		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		String loginId = (String) payload.get("user_id");

		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;

		if (login) {
			success = service.productUpdate(dto);
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
		@RequestHeader Map<String, String> header
	) {
		log.info("이미지 수정 요청: product_idx={}, files={}", product_idx, files != null ? files.length : 0);
		Map<String, Object> result = new HashMap<>();

		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		String loginId = (String) payload.get("user_id");

		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;

		if (login) {
			success = service.updateProductImg(product_idx, files);
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

		Map<String, Object> result = new HashMap<>();

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

		Map<String, Object> result = new HashMap<>();

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

	@PostMapping("/product/list")
	public Map<String, Object> productList(@RequestBody Map<String, Object> param) {
		return service.productList(param);
	}
}