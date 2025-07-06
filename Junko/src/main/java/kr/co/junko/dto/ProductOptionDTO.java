package kr.co.junko.dto;

import lombok.Data;

@Data
public class ProductOptionDTO {

    private int product_option_idx;
    private int product_idx;
    private int combined_idx;
    private int min_cnt;
    private boolean del_yn;

}