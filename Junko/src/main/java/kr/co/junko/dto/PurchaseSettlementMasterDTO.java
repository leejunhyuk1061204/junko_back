package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PurchaseSettlementMasterDTO {
	// 매입 정산 마스터
	
	private int ps_master_idx; 				// 정산 마스터 번호
	private int custom_idx; 					// 거래처 번호
	private String settlement_cycle;		// 정산주기
	private String settlement_method;	// 정산 방식
	private String use_yn;					// 활성 여부
	
	
}

