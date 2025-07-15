package kr.co.junko.dto;

import lombok.Data;

@Data
public class OptionDTO {

    private int option_idx;
    private String option_name;
    private String option_value;
    private int del_yn;
    
    private Integer using_idx;

}