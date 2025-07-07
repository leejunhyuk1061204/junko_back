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
	
	// 송장 자동 생성 시 입력값
	private int custom_idx;
	
	// 출고 자동 생성용 waybill_idx 받아오기
	private int waybill_idx;
	
}
