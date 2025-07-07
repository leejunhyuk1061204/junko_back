package kr.co.junko.dto;

import lombok.Data;

@Data
public class CustomDTO {

    private int custom_idx;
    private String custom_name;
    private String custom_owner;
    private String custom_phone;
    private String custom_fax;
    private String custom_type;
    private String business_number;
    private String account_number;
    private String bank;
    private int del_yn;
	
}
