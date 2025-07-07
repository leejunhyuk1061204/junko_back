package kr.co.junko.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class ApprovalLineDTO {

	private int approval_idx;
	private int document_idx;
	private int user_idx;
	private int step;
	private String status;
	private String comment;
	private Date approved_date;
	private int del_yn;
	
}