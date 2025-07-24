package kr.co.junko.timecard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.TimecardDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class TimecardService {
	
	private final TimecardDAO dao;

//	public boolean attendInsert(int user_idx) {
//		if (dao.checkTodayAttend(user_idx, List.of("출근", "지각"))>0) {
//			return false;
//		}
//		String status = LocalTime.now().isAfter(LocalTime.of(9, 0)) ? "지각":"출근";
//		return dao.attendInsert(user_idx, status)>0;
//	}
//
//	public boolean endTimeInsert(int user_idx) {
//		return dao.endTimeInsert(user_idx, "퇴근")>0;
//	}
//
//	public boolean leaveStatusInsert(int user_idx, String status) {
//		return dao.leaveStatusInsert(user_idx, status)>0;
//	}
//	
//	public List<Map<String, Object>> attendList(int user_idx) { 
//		return dao.attendList(user_idx); 
//	}
//
//	public List<Map<String, Object>> attendListDept(int dept_idx) {
//		return dao.attendListDept(dept_idx);
//	}
//
//	public List<Map<String, Object>> attendListDate(String start_date, String end_date) {
//		Map<String, Object> param = new HashMap<String, Object>();
//	    param.put("start_date", start_date);
//	    param.put("end_date", end_date);
//	    return dao.attendListDate(param);
//	}
//
//	public boolean timecardUpdate(TimecardDTO dto) {
//		return dao.timecardUpdate(dto)>0;
//	}

	public Map<String, Object> timecardList(Map<String, Object> param) {
		Map<String, Object>result = new HashMap<String, Object>();
		if(param.get("page") != null) {
			int cnt = 10;
			int offset = ((int)param.get("page")-1)*cnt;
			param.put("cnt", cnt);
			param.put("offset", offset);
			int total = dao.timecardListTotalPage(param);
			result.put("totla", total);
		}
		List<Map<String, Object>>list = dao.timecardList(param);
		result.put("list", list);
		return result;
	}
	
	// auto 결근, 미퇴근
	@Scheduled(cron = "00 55 23 ? * 2-6",zone="Asia/Seoul")
	public void autoUpdateTimecard() {
		log.info("자동입력 시작: " + LocalDateTime.now());
		List<Integer>userIdxList = dao.getUserIdxList();
		TimecardDTO dto = null;
		for (int idx : userIdxList) {
				int row = dao.searchTimecardByIdx(idx); // 오늘 날짜, user_idx로 입력된 리스트 있는지 검색
				if(row == 0) {
					dto = new TimecardDTO();
					dto.setUser_idx(idx);
					dto.setWork_date(LocalDate.now().minusDays(1));
					dto.setStatus("결근");
					dao.timecardInsert(dto);
				}
		}
	}

	public boolean timecardInsert(TimecardDTO dto) {
		LocalDate work_date = LocalDate.now();
		LocalTime work_time = LocalTime.now();
		
		switch (dto.getStatus()) {
		case "출근":
			dto.setWork_date(work_date);
			dto.setWork_time(work_time);
			if(work_time.isAfter(LocalTime.of(9, 0))) {
				dto.setStatus("지각");
			}
			return dao.timecardInsert(dto)>0;
			
		case "퇴근":
			dto.setWork_date(work_date);
			dto.setWork_time(work_time);
			return dao.timecardInsert(dto)>0;
		
		default: // 연차, 반차, 출장, 외근 은 그냥 입력되면 됨
			return dao.timecardInsert(dto)>0;
		}
	}

	public boolean timecardUpdate(TimecardDTO dto) {
		return dao.timecardUpdate(dto)>0;
	}
}
