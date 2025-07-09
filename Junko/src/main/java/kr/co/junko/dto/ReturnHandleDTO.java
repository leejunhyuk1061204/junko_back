package kr.co.junko.dto;

import lombok.Data;

@Data
public class ReturnHandleDTO {

	private int return_idx;
	private int return_receive_idx;
	private int disposal_reson;
	private int handle_date;
	private int user_idx;
	private int product_idx;
	private int product_option_idx;
	private int disposal_cnt;
	private int resell_cnt;
	private boolean del_yn;
	
}
