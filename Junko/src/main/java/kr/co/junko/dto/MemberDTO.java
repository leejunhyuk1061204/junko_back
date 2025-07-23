package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MemberDTO {

	private int user_idx;
	private String user_id;
	private String pw;
	private String user_name;
	private String email;
	private String phone;
	private String address;
	private LocalDate hire_date;
	private String status;
	private int anuual_cnt;
	private int job_idx;
	private int dept_idx;
	
	private String job_name;
	
}

