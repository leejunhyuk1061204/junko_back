package kr.co.junko.dto;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class MsgDTO {

    private int msg_idx;
    private int sender_idx;
    private int receiver_idx;
    private String msg_title;
    private String msg_content;
    private Date sent_at;
    private int receiver_del;
    private int sender_del;
    private int read_yn;
    private int important_yn;
    
    private String role;
    
    private List<Integer> receiver_list; // 수신자 여러 명용 리스트
	
}
