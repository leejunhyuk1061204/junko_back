package kr.co.junko.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TaxInvoiceDTO {

	 private int invoice_idx;      // 세금계산서 ID (PK)
	    private Integer custom_idx;   // 거래처 ID
	    private Integer entry_idx;    // 연동된 전표 ID
	    private int total_amount;     // 총 금액
	    private String status;        // 상태 (예: 작성중, 발행, 취소)
	    private LocalDateTime reg_date;
	    private LocalDateTime mod_date;
	    private String issued_by;     // 발행자
	    private boolean del_yn;

	
	
	
}
