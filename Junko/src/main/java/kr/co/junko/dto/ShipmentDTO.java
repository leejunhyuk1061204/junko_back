package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ShipmentDTO {

	private int shipment_idx;
	private int user_idx;
	private int sales_idx;
	private int waybill_idx;
	private LocalDate shipment_date;
	private String status;
	private boolean del_yn;
	
}
