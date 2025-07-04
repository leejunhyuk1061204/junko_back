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
	
	private Integer custom_idx;
	// 고객명 - 매출,환불일 경우 거래처 항목이 null이기에 가져옴
	
	private Integer sales_idx;
	// 거래처 - 매입일 경우 매출,환불이 null이기 때문에 가져옴
}

