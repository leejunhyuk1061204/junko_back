package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CollectionAndPaymentResponseDTO {
	
	private int cap_idx;
    private String type;
    private LocalDate date;
    private int amount;

    // 거래처 정보
    private int custom_idx;
    private String customName;
    private String accountBank;
    private String accountNumber;

    // 전표 정보
    private int entry_idx;
    private String entryTitle;

    private String memo;
    private boolean deleted;
}
