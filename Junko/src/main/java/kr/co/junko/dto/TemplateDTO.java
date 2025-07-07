package kr.co.junko.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class TemplateDTO {

	private int template_idx;
	private int user_idx;
	private String template_name;
	private String template_desc;
	private String template_html;
	private Date created_date;
	private Date update_date;
	private int del_yn;
	
}
