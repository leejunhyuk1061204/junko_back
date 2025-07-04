package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OrderDTO {

	private int order_idx;
	private int custom_idx;
	private int warehouse_idx;
	private LocalDate reg_date;
	private int user_idx;
	private String status;
	private boolean del_yn;
	
}
