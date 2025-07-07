package kr.co.junko.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class OrderPlanDTO {

	private int plan_idx;
	private LocalDate delivery_date;
	private boolean del_yn;
	private int order_idx;
	
	List<PlanProductDTO>planProduct;
}
