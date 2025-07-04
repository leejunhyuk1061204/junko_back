package kr.co.junko.dto;

import java.sql.Date;
import java.time.LocalDate;

import lombok.Data;

@Data
public class AccountingEntryDTO {
// 회계전표 기록
	
	private int entry_idx;			// 전표번호
	private int account_idx;		// 입고번호 / 주문번호
	private String entry_type;	// 전표유형 (매입,매출,환불)
	private int amount;			// 금액
	private Date entry_date;		// 전표생성일자
	
	
}

