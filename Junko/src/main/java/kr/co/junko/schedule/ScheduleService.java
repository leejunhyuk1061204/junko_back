package kr.co.junko.schedule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.ScheduleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

	private final ScheduleDAO dao;

	public boolean scheduleInsert(ScheduleDTO dto) {
		String label_name = dao.getLabelName(dto.getLabel_idx());
		log.info("label_name: " + label_name + ", label_idx: " + dto.getLabel_idx());
		int row = 0;
		
		if (label_name != null && dto.getStart_date() != null && dto.getEnd_date() != null) {
			row = dao.scheduleInsert(dto);
	    }	
		return row>0;
	}
	
	public int userIdxByLoginId(String loginId) {
		return dao.userIdxByLoginId(loginId);
	}

	public boolean scheduleUpdate(ScheduleDTO dto) {
		return dao.scheduleUpdate(dto)>0;
	}

	public boolean scheduleDelete(int schedule_idx, int user_idx) {
		return dao.scheduleDelete(schedule_idx, user_idx)>0;
	}

	public List<Map<String, Object>> scheduleList(Map<String, Object> param) {
		String type = (String) param.get("type"); // personal, dept, work
		List<Map<String, Object>> result;
		
		switch (type) {
			case "personal":
				result = dao.scheduleListPersonal(param);
				break;
			case "dept":
				result = dao.scheduleListDept(param);
				break;
			case "work":
				result = dao.scheduleListStatus(param);
				break;
			default:
				result = Collections.emptyList(); // 예외 처리
				break;

		}		
		return result;
	}

	public MemberDTO getUserInfo(int user_idx) {
		return dao.getUserInfo(user_idx);
	}
	
}
