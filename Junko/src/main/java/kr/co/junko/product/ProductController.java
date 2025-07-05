package kr.co.junko.product;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class ProductController {

	@Autowired ProductService service;
	
	Map<String, Object> result = null;
	
	// 상품 등록
	@PostMapping(value="/product/insert")
	public Map<String, Object>productInsert(@ModelAttribute ProductDTO dto){
		
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		
		MultipartFile[] files = dto.getImages();
		
		if (files == null) {
			files = new MultipartFile[] {};
		}
		
		// 상품 등록
		boolean success = service.productInsert(files, dto);
		result.put("success", success);
		result.put("idx", dto.getProduct_idx());
		
		return result;
	}
	
	// 상품 CSV 파일 등록
	@PostMapping(value="/product/csv")
	public Map<String, Object> productCsvInsert(@RequestParam("file") MultipartFile file) {
		log.info("CSV file : {}", file);
		result = new HashMap<String, Object>();
		
		// CSV 파일 등록
		boolean success = service.productCsvInsert(file);
		result.put("success", success);
		
		return result;
	}

	// 상품 이미지만 등록
	@PostMapping("/product/{product_idx}/imgInsert")
	public Map<String, Object> uploadProductImg(
		@PathVariable("product_idx") int product_idx,
		@RequestParam("images") MultipartFile[] files
	) {
		Map<String, Object> result = new HashMap<>();
		boolean success = service.productImgInsert(product_idx, files);
		result.put("success", success);
		return result;
	}

	// 상품 수정
	@PutMapping("/product/update")
	public Map<String, Object> updateProduct(@ModelAttribute ProductDTO dto) {
		log.info("dto: {}", dto);
		Map<String, Object> result = new HashMap<>();

		boolean success = service.productUpdate(dto);
		result.put("success", success);
		return result;
	}

	// 상품 이미지 수정
	// 기존 이미지 소프트 삭제 후 새 이미지 등록
	@PutMapping("/product/{product_idx}/imgUpdate")
	public Map<String, Object> updateProductImg(
		@PathVariable("product_idx") int product_idx,
		@RequestParam(value = "images", required = false) MultipartFile[] files
	) {
		log.info("이미지 수정 요청: product_idx={}, files={}", product_idx, files != null ? files.length : 0);
		boolean success = service.updateProductImg(product_idx, files);
		return Map.of("success", success);
	}

	// 상품 이미지 소프트 삭제
	@PutMapping("/product/{product_idx}/imgDel")
	public Map<String, Object> softDelProductImg(@PathVariable("product_idx") int product_idx) {
		boolean success = service.softDelProductImg(product_idx);
		return Map.of("success", success);
	}

	// 상품 자체 소프트 삭제
	@PutMapping("/product/{product_idx}/del")
	public Map<String, Object> softDelProduct(@PathVariable("product_idx") int product_idx) {
		boolean success = service.softDelProduct(product_idx);
		return Map.of("success", success);
	}

}