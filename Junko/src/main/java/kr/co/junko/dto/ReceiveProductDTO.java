package kr.co.junko.dto;

import lombok.Data;

@Data
public class ReceiveProductDTO {

	private int receive_product_idx;
	private int receive_idx;
	private int product_idx;
	private int product_option_idx;
	private int receive_cnt;
	private boolean del_yn;
	
}
