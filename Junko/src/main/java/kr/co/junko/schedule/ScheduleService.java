package kr.co.junko.schedule;

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

	public boolean scheduleDelete(int schedule_idx) {
		return dao.scheduleDelete(schedule_idx)>0;
	}

	public List<Map<String, Object>> scheduleList(Map<String, Object> param) {
		int label_idx = (int) param.get("label_idx");
		List<Map<String, Object>> result;
		
		if (label_idx == 1) {
			result = dao.scheduleListPersonal(param);
		}else {
			result = dao.scheduleListDept(param);
		}
		return result;
	}

	public MemberDTO getUserInfo(int user_idx) {
		return dao.getUserInfo(user_idx);
	}
	
}
