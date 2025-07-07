package kr.co.junko.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CollectionAndPaymentLogDTO {
	
	    private int log_Idx;
	    private int cap_Idx;
	    private String actionType; // 수정 or 삭제
	    private String beforeData; // JSON 문자열
	    private String afterData;  // JSON 문자열
	    private LocalDateTime regDate;
	

}
