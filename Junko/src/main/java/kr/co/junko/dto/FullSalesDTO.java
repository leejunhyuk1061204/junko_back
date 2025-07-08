package kr.co.junko.dto;

import java.util.List;

import lombok.Data;

@Data
public class FullSalesDTO {

	private SalesDTO sales;
	private List<SalesProductDTO> products;
	
}
