package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CollectionAndPaymentRequestDTO {

	private int cap_idx;
	private String type; // "수금" or "지급"
    private LocalDate date;
    private int amount;
    private int custom_idx; // 거래처
    private int entry_idx; // 전표 (nullable)
    private String memo; // 비고 (optional)
	
	
}
