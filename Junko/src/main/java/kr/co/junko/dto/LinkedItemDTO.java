package kr.co.junko.dto;

import lombok.Data;

@Data
public class LinkedItemDTO {

	 private int idx;      // 전표 / 정산 / 세금계산서의 고유번호
	    private String title; // 문서 제목
	    private String type;  // "entry" / "settlement" / "invoice"
	
}
