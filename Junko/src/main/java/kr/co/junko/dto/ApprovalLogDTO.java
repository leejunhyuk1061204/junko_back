package kr.co.junko.dto;

import java.util.Date;
import lombok.Data;

@Data
public class ApprovalLogDTO {
    private int log_id;
    private int document_idx;
    private int user_idx;
    private String status; // 승인, 반려
    private String comment;
    private Date approved_date;
    
    private String user_name;
    
}
