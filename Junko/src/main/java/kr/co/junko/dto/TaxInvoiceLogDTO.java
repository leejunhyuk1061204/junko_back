package kr.co.junko.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TaxInvoiceLogDTO {

	 private int log_idx;          // 로그 ID
	    private int invoice_idx;      // 세금계산서 ID (FK)
	    private String status;        // 변경된 상태
	    private String status_by;     // 변경한 사용자
	    private LocalDateTime status_time;
	    private String memo;          // 변경 사유 (선택)
	
}
