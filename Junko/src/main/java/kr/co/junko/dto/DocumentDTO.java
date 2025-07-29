package kr.co.junko.dto;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DocumentDTO {

	private int document_idx;
	private int user_idx;
	private String content;
	private Date created_date;
	private String status;
	private int del_yn;
	private int template_idx;
	private int idx;
	private String type;
	
	private String template_name;
	private String user_name;
	private Map<String, String> variables;
	private List<Map<String, String>>variablesList;
	private String approver_name;
}
