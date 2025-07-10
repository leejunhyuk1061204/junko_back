package kr.co.junko.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.ScheduleDTO;
import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ScheduleController {
	
	private  final ScheduleService service;
	Map<String, Object> result = null;
	
	// 일정 등록
	@PostMapping("/schedule/insert")
	public Map<String, Object> scheduleInsert(@RequestBody ScheduleDTO dto,
			@RequestHeader Map<String, String> header) {
		log.info("dto : {}", dto);
		result = new HashedMap<String, Object>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		boolean success = false;
		boolean login = false;
		
		if (loginId != null && !loginId.isEmpty()) {
			int user_idx = service.userIdxByLoginId(loginId);
			dto.setUser_idx(user_idx);
			success = service.scheduleInsert(dto);
			login = true;
		}
		result.put("success", success);
		result.put("loginYN", login);
		return result;
	}
	
	// 일정 수정
	@PostMapping("/schedule/update")
	public Map<String, Object> scheduleUpdate(@RequestBody ScheduleDTO dto,
			@RequestHeader Map<String, String> header) {
		log.info("dto : {}", dto);
		result = new HashedMap<String, Object>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		boolean success = false;
		boolean login = false;
		
		if (loginId != null && !loginId.isEmpty()) {
			int user_idx = service.userIdxByLoginId(loginId);
			dto.setUser_idx(user_idx);
			success = service.scheduleUpdate(dto);
			login = true;
		}
		result.put("success", success);
		result.put("loginYN", login);
		return result;
	}
	
	// 일정 삭제
	@PutMapping("/schedule/del")
	public Map<String, Object> scheduleDelete(@RequestParam int schedule_idx,
			@RequestHeader Map<String, String> header) {
		log.info("일정 삭제 : "+schedule_idx);
		result = new HashedMap<String, Object>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		boolean success = false;
		boolean login = false;
		
		if (loginId != null && !loginId.isEmpty()) {
			success = service.scheduleDelete(schedule_idx);
			login = true;
		}
		result.put("success", success);
		result.put("loginYN", login);
		return result;
	}
	
	// 일정 불러오기
	@PostMapping("/schedule/list")
	public Map<String, Object> scheduleList(@RequestBody Map<String, Object> param,
			@RequestHeader Map<String, String> header) {
		log.info("스케줄 리스트 : {}",param);
		result = new HashedMap<String, Object>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		boolean login = false;
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		if (loginId != null && !loginId.isEmpty()) {
			int user_idx = service.userIdxByLoginId(loginId);
			param.put("user_idx", user_idx);
			
			MemberDTO member = service.getUserInfo(user_idx);
			param.put("dept_idx", member.getDept_idx());
			
			list = service.scheduleList(param);
			login = true;
		}
		result.put("list", list);
		result.put("loginYN", login);
		return result;
	}

}
