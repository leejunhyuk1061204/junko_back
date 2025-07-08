package kr.co.junko.dto;

import lombok.Data;

@Data
public class SalesProductDTO {

	private int sales_product_idx;
	private int sales_idx;
	private int product_idx;
	private int product_option_idx;
	private int product_cnt;
	private int product_price;
	private boolean del_yn;
	
}
