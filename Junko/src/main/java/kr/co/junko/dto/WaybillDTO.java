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
	
	// 송장 등록 시 입력받을 값
	// custom_idx(택배사), warehouse_idx(출고할 창고)
	private int warehouse_idx;
	
}
