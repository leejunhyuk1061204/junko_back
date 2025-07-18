package kr.co.junko.dto;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class VoucherDTO {

    private int entry_idx;
    private int account_idx;
    private String entry_type;
    private int amount;
    private Date entry_date;
    private int custom_idx;
    private int sales_idx;
    private int user_idx;
    private int del_yn;
    private String status;

    private List<EntryDetailDTO> entry_details;
}
