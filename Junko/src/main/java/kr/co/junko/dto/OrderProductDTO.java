package kr.co.junko.dto;

import lombok.Data;

@Data
public class OrderProductDTO {

	private int order_product_idx;
	private int order_idx;
	private int product_idx;
	private int order_cnt;
	private boolean del_yn;
	
}
