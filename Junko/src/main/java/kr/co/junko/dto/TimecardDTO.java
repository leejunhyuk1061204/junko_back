package kr.co.junko.dto;

import java.sql.Date;
import java.time.LocalTime;

import lombok.Data;

@Data
public class TimecardDTO {
	
	private int attend_idx;
	private int user_idx;
	private Date work_date;
	private LocalTime start_time;		// 출근시간
	private LocalTime end_time;		// 퇴근시간
	private String status;					// 근무 상태 (출근,지각,결근)
	private String leave_status;		// 연차,반차 상태 (오전반차,오후반차)
	
}
