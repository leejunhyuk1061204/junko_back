package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class LotDTO {

	private int lot_idx;
	private LocalDate manufacture;
	private LocalDate expiration;
	
}
