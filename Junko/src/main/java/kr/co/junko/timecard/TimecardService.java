package kr.co.junko.timecard;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.TimecardDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TimecardService {
	
	private final TimecardDAO dao;

	public boolean attendInsert(int user_idx) {
		if (dao.checkTodayAttend(user_idx, List.of("출근", "지각"))>0) {
			return false;
		}
		String status = LocalTime.now().isAfter(LocalTime.of(9, 0)) ? "지각":"출근";
		return dao.attendInsert(user_idx, status)>0;
	}

	public boolean endTimeInsert(int user_idx) {
		return dao.endTimeInsert(user_idx, "퇴근")>0;
	}

	public boolean leaveStatusInsert(int user_idx, String status) {
		return dao.leaveStatusInsert(user_idx, status)>0;
	}
	
	public List<Map<String, Object>> attendList(int user_idx) { 
		return dao.attendList(user_idx); 
	}

	public List<Map<String, Object>> attendListDept(int dept_idx) {
		return dao.attendListDept(dept_idx);
	}

	public List<Map<String, Object>> attendListDate(String start_date, String end_date) {
		Map<String, Object> param = new HashMap<String, Object>();
	    param.put("start_date", start_date);
	    param.put("end_date", end_date);
	    return dao.attendListDate(param);
	}

	public boolean timecardUpdate(TimecardDTO dto) {
		return dao.timecardUpdate(dto)>0;
	}
	
}
