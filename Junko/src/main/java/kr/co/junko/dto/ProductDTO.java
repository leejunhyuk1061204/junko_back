package kr.co.junko.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ProductDTO {

	private int product_idx;
	private String product_name;
	private int purchase_price;
	private int selling_price;
	private int discount_rate;
	private String product_standard;
	private int category_idx;
	private boolean del_yn; // 삭제여부
	private int min_cnt;
	
	private MultipartFile[] images; // 여러 장 이미지 업로드용
	
	private String category_name;

	private List<String> imageUrls; // 이미지 URL 목록
	
}
