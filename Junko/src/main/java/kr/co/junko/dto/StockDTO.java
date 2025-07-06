package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class StockDTO {

	private int stock_idx;
	private int product_idx;
	private int product_option_idx;
	private int stock_cnt;
	private LocalDate manufacture;
	private LocalDate expiration;
	private int warehouse_idx;
	private int zone_idx;
	private String type;
	private int user_idx;
	private boolean del_yn;
	
}
