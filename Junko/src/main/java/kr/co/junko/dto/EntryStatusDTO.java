package kr.co.junko.dto;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class EntryStatusDTO {

    private int settlement_id;       
    private int entry_idx;           
    private int custom_idx;           
    private Date settlement_day;      
    private int total_amount;         
    private int amount;               
    private String status;            
    private int del_yn;  
    
    private List<Integer> entry_idx_list;
    private String custom_name;
    private int user_idx;

    private String entry_type;
    private Date entry_date;
    private String user_name;

    private int document_idx;
    
}
