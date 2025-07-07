package kr.co.junko.dto;

import lombok.Data;

@Data
public class PlanProductDTO {

	private int plan_product_idx;
	private int plan_idx;
	private int order_product_idx;
	private int order_cnt;
	private boolean del_yn;
	
	private String productTempId; // product 매칭용 임시ID
	
}
