package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CapSearchDTO {
	
	 private String type;          // "수금" or "지급"
	    private Integer minAmount;    // 최소 금액
	    private Integer maxAmount;    // 최대 금액
	    private LocalDate startDate;  // 시작일
	    private LocalDate endDate;    // 종료일
	    private String keyword;       // 거래처명 or 메모 검색어
	    private String sortBy;        // 정렬 기준: "date", "amount"
	    private String sortOrder;     // 정렬 방향: "asc", "desc"
	
	    private int limit;
	    private int offset;
	    
	    
}
