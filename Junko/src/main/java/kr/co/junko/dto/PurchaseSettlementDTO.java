package kr.co.junko.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

import lombok.Data;

@Data
public class PurchaseSettlementDTO {
// 매입 정산
	
	private int settlement_idx;				// 정산번호
	private int entry_idx;						// 전표idx
	private int custom_idx;					// 거래처번호
	private int amount;						// 잔액
	private String settlement_day;			// 정산일자
	private BigDecimal total_amount;		// 정산금액
	private String status;						// 정산상태
	private boolean del_yn; // 삭제여부
	
}

