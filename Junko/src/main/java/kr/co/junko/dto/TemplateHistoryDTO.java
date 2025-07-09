package kr.co.junko.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class TemplateHistoryDTO {

    private int history_idx;
    private int template_idx;
    private int user_idx;
    private String template_name;
    private String template_desc;
    private String template_html;
    private Date saved_at;
	
}
