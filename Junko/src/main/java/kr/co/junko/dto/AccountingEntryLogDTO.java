package kr.co.junko.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AccountingEntryLogDTO {

	    private int log_idx;             // 로그 PK
	    private int entry_idx;           // 전표번호
	    private String user_id;          // 변경자
	    private String action;           // 상태변경/수정/첨부 등 행동
	    private String before_status;    // 변경 전 상태
	    private String after_status;     // 변경 후 상태
	    private String log_message;      // 사유/비고(옵션)
	    private LocalDateTime created_at;// 로그 시각
	
	
}
