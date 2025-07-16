package kr.co.junko.dto;

import java.util.List;

import lombok.Data;

@Data
public class CategoryDTO {

	private int category_idx;
	private String category_name;
	private Integer category_parent; // null 허용

	private List<CategoryDTO> children;
	
	private int category_order;
	
}
