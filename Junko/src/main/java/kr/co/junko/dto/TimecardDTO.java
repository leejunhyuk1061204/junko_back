package kr.co.junko.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class TimecardDTO {
	
	private int attend_idx;
	private int user_idx;
	private LocalDate work_date;
	private LocalTime work_time;
	private String status;					// 근무 상태 (출근,지각,결근)
	
}
