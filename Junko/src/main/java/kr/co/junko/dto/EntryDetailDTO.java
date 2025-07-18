package kr.co.junko.dto;

import lombok.Data;

@Data
public class EntryDetailDTO {
    
    private int dept_idx;
    private int entry_idx;
    private int as_idx;
    private int amount;
    private String type;
    private int del_yn;

}
