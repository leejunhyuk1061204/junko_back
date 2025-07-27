package kr.co.junko.dto;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class ReceiptPaymentDTO {
    
    private int rp_idx;
    private String type; // "수금" or "지급"
    private Integer entry_idx;
    private int custom_idx;
    private int amount;
    private String method;
    private Date transaction_date;
    private String note;
    private String status; // 작성중 / 확정 / 취소
    private int del_yn;
    private Date reg_date;
    private Date mod_date;
    private int user_idx;

    private String customer_name;

    private int template_idx;
    private List<Integer> approver_ids;
    private List<ApprovalLineDTO> approval_lines;

    private int document_idx;
    
}
