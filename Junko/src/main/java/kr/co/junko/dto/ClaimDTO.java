package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ClaimDTO {

	private int claim_idx;
	private int	sales_idx;
	private String	type;
	private String claim_reason;
	private LocalDate claim_date;
	private String status;
	private boolean del_yn;
	
	// 반품송장 자동생성 시 입력값
	private int custom_idx;
	
	// 반품 자동 생성을 위한 입력값
	private int warehouse_idx;
	
	// 클레임 반품, 교환 처리 여부
	private boolean returnStatus;
}
