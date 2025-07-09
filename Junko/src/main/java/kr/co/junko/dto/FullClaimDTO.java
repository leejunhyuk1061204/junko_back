package kr.co.junko.dto;

import java.util.List;

import lombok.Data;

@Data
public class FullClaimDTO {

	private ClaimDTO claim;
	private List<ReturnProductDTO> products;
	
}
