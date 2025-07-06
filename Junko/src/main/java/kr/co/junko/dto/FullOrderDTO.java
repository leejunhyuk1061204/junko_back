package kr.co.junko.dto;

import lombok.Data;

@Data
public class FullOrderDTO {

	private OrderDTO order;
	private OrderProductDTO orderProduct;
	private OrderPlanDTO orderPlan;
	
}
