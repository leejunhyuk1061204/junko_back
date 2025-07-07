package kr.co.junko.dto;

import lombok.Data;

@Data
public class TemplateVarDTO {

	private int variable_idx;
	private int template_idx;
	private String variable_name;
	private int del_yn;
	
}
