package kr.co.junko.dto;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class InvoiceTaxDTO {
    
    private int invoice_idx;
    private int custom_idx;
    private int entry_idx;
    private int total_amount;
    private String status;
    private Date reg_date;
    private Date mod_date;
    private String issued_by;
    private int del_yn;

    private List<InvoiceDetailDTO> details;  // 디테일 리스트 포함
    
    private String custom_name;
    
    private Integer document_idx;
    private String file_name;      
    
    private int user_idx;

}
