package kr.co.junko.dto;

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
	
}
