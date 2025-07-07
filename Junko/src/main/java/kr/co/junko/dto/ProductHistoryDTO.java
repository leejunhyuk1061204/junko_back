package kr.co.junko.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class ProductHistoryDTO {

    private int history_idx;
    private int product_idx;
    private String product_name;
    private int purchase_price;
    private int selling_price;
    private int discount_rate;
    private String product_standard;
    private String status;
    private int updated_by;
    private Date updated_at;
	
}
