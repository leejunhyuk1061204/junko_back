package kr.co.junko.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class FileDTO {

	private int file_idx;
	private String ori_filename;
	private String new_filename;
	private Date reg_date;
	private String type;
	private int idx;
	private boolean del_yn; // 삭제여부
	
}
