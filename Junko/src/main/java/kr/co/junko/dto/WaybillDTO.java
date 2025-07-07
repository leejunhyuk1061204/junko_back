package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class WaybillDTO {

	private int waybill_idx;
	private int sales_idx;
	private String status;
	private int custom_idx;
	private LocalDate waybill_date;
	private boolean del_yn;
	
}
