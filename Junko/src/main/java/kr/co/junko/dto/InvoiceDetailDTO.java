package kr.co.junko.dto;

import lombok.Data;

@Data
public class InvoiceDetailDTO {
    
    private int detail_idx;
    private int invoice_idx;
    private String item_name;
    private int quantity;
    private int price;
    private int total_amount;

}
