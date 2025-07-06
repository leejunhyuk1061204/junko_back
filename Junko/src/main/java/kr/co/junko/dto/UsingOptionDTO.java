package kr.co.junko.dto;

import lombok.Data;

@Data
public class UsingOptionDTO{

    private int using_idx;
    private int product_idx;
    private int option_idx;
    private boolean del_yn;

    private String option_name;
    private String option_value;
    
}