package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ClaimHandleDTO {

	private int claim_handle_idx;
	private int claim_idx;
	private int user_idx;
	private LocalDate handle_date;
	private String handle_detail;
	private boolean del_yn;
	
	// 클레임 상태 변경을 위한 입력값
	private String status;

	// 클레임 반품, 교환 처리 여부
	private boolean returnStatus;
	
	// 클레임 처리완료 시 를 위한 입력값
	private int warehouse_idx;

	// 송장 자동생성을 위한 입력값
	private int custom_idx;
	

}
