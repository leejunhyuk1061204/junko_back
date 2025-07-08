package kr.co.junko.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminLogDTO {
	
	private int log_idx;
	private int admin_idx;
	private String log_type;
	private String target_table;
	private String operation_detail;
	private LocalDateTime log_time;
	private String ip_address;

}
