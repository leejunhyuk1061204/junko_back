package kr.co.junko.dto;

import java.sql.Date;
import java.time.LocalDate;

import lombok.Data;

@Data
public class CollectionAndPaymentDTO {
// 수금 / 지급
	
	private int cap_idx;	//이력번호
	private String type;	// 수금 /지급 - enum
	private int amount;	// 금액
	private Date date;		// 일자
	private String target;	// 거래대상
	
}

