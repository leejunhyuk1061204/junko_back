package kr.co.junko.dto;

import java.util.List;

import lombok.Data;

@Data
public class FullOrderDTO {

	private OrderDTO order;
	private List<OrderProductDTO> orderProduct;
	private List<OrderPlanDTO> orderPlan;
	
}
