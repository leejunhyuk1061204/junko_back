package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReturnHandleDTO {

	private int return_handle_idx;
	private int return_receive_idx;
	private String disposal_reason;
	private LocalDate handle_date;
	private int user_idx;
	private int product_idx;
	private int product_option_idx;
	private int disposal_cnt;
	private int resell_cnt;
	private boolean del_yn;

	// 재고등록을 위한 입력값
	private int zone_idx;
	private LocalDate manufacture;
	private LocalDate expiration;
	
}
