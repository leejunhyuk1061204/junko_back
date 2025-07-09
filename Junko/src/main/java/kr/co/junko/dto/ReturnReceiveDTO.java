package kr.co.junko.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReturnReceiveDTO {

	private int return_receive_idx;
	private int claim_idx;
	private int return_waybill_idx;
	private int warehouse_idx;
	private int user_idx;
	private String status;
	private boolean del_yn;
	
	// 반품 처리 입력을 위해 입력받는 값
	// return_receive_idx, disposal_reson, user_idx, product_idx,product_option_idx, disposal_cnt, resell_cnt
	List<ReturnHandleDTO>handle;
	
}
