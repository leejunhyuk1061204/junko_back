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
	
}
