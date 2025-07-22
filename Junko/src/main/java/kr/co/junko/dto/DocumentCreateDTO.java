package kr.co.junko.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DocumentCreateDTO {

	private int template_idx;
	private int user_idx;
	private Map<String, String> variables; // key: 변수명, value: 입력값
	private List<Integer> approver_ids;    // 결재자 user_idx 리스트
	private int idx;
	private String type;
}
