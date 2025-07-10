package kr.co.junko.dto;

import java.sql.Date;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ScheduleDTO {
	
	private int schedule_idx;
	private int user_idx;
	private String title;
	private String description;
	private String start_time;
	private String end_time;
	private Date start_date;
	private Date end_date;
	private int label_idx;
	private boolean del_yn;

}
