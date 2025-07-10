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
	// return_receive_idx, user_idx
	// 리스트 : product_idx,product_option_idx, resell_cnt, disposal_cnt, disposal_reason
	// resell_cnt > 0 이면 재고등록을 위해 아래 3개도 입력해야 함
	// zone_idx , manufacture , expiration
	private List<ReturnHandleDTO>handle;
	
}
