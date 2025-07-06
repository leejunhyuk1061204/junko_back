package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReceiveDTO {

	private int receive_idx;
	private int order_idx;
	private int user_idx;
	private LocalDate receive_date;
	private String status;
	private boolean del_yn;
	
}
