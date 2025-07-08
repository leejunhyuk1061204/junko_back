package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReturnWaybillDTO {

	private int return_waybill_idx;
	private int claim_idx;
	private LocalDate pickup_req_date;
	private LocalDate pickup_com_date;
	private String status;
	private boolean del_yn;
	private int custom_idx;
}
