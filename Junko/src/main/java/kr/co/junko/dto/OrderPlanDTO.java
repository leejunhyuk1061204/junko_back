package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OrderPlanDTO {

	private int plan_idx;
	private int order_product_idx;
	private int order_cnt;
	private LocalDate delivery_date;
	private boolean del_yn;
	
}
