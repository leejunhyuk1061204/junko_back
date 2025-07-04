package kr.co.junko.product;

import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.jni.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
}
