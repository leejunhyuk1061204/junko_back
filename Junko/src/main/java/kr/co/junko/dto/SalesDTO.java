package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SalesDTO {

	private int sales_idx;
	private String customer;
	private String customer_phone;
	private String customer_address;
	private String payment_option;
	private LocalDate payment_date;
	private String status;
	private boolean del_yn;
	
}
