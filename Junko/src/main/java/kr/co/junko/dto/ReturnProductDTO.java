package kr.co.junko.dto;

import lombok.Data;

@Data 
public class ReturnProductDTO {

	private int return_product_idx;
	private int claim_idx;
	private int product_idx;
	private int product_option_idx;
	private int return_cnt;
	private boolean del_yn;
	private int exchange_idx;
}
