package kr.co.junko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AccountingDepartmentDTO {
	// 회계분개
	
	private int dept_idx;	// 분개번호
	private int entry_idx;
	private int as_idx;		// 계정과목번호
	private int amount;	// 금액
	private String type;	// 종류 (차변,대변)
	private boolean del_yn; // 삭제여부
	
	// 계정과목
	private String as_name;	// 계정과목명

	public void setUser_idx(int user_idx) {
		// TODO Auto-generated method stub
		
	}
		
	private Integer file_idx;
	private String ori_filename;
	
}

