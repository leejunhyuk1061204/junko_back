package kr.co.junko.dto;

import lombok.Data;

@Data
public class TaxInvoiceDetailDTO {

	private int detail_idx;       // 품목 상세 ID (PK)
    private int invoice_idx;      // 연동된 세금계산서 ID (FK)
    private String item_name;     // 품목명
    private int quantity;         // 수량
    private int price;            // 단가
    private int total_amount;     // 총 금액 = 수량 * 단가
	
	
	
}
