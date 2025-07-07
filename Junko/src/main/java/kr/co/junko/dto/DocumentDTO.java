package kr.co.junko.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class DocumentDTO {

	private int document_idx;
	private int user_idx;
	private String content;
	private Date created_date;
	private String status;
	private int del_yn;
	
}
