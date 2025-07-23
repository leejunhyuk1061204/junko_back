package kr.co.junko.dto;

import lombok.Data;

@Data
public class DocumentVarDTO {

    private int document_variable_idx; // optional
    private int document_idx;
    private String var_key;
    private String var_value;
    private int del_yn = 0;
	
}
