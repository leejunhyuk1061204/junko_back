package kr.co.junko.timecard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.TimecardDTO;
import kr.co.junko.schedule.ScheduleService;
import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class TimecardController {
	
	private final TimecardService service;
	private final ScheduleService schservice;
	Map<String, Object> result = null;
	boolean success = false;
	boolean login = false;
	
//	// 출근 처리
//	@PostMapping("/timecard/in")
//	public Map<String, Object> attendInsert(@RequestHeader Map<String, String> header) {
//		result = new HashMap<String, Object>();
//		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
//		
//		if (loginId != null && !loginId.isEmpty()) {
//			int user_idx = schservice.userIdxByLoginId(loginId);
//			success = service.attendInsert(user_idx);
//			login = true;
//		}
//		result.put("success", success);
//		result.put("loginYN", login);		
//		return result;
//	}
//	
//	// 퇴근 처리
//	@PostMapping("/timecard/out")
//	public Map<String, Object> endTimeInsert(@RequestHeader Map<String, String> header) {
//		result = new HashMap<String, Object>();
//		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
//		
//		if (loginId != null && !loginId.isEmpty()) {
//			int user_idx = schservice.userIdxByLoginId(loginId);
//			success = service.endTimeInsert(user_idx);
//			login = true;
//		}
//		result.put("success", success);
//		result.put("loginYN", login);
//		return result;
//	}
//	
//	// 연차, 반차 등록
//	@PostMapping("/timecard/leave")
//	public Map<String, Object> leaveStatusInsert(@RequestBody Map<String, Object> param,
//			@RequestHeader Map<String, String> header) {
//		result = new HashMap<String, Object>();
//		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
//		
//		if (loginId != null && !loginId.isEmpty()) {
//			int user_idx = schservice.userIdxByLoginId(loginId);
//			String status = (String) param.get("status");
//			success = service.leaveStatusInsert(user_idx, status);
//			login = true;
//		}
//		result.put("success", success);
//		result.put("loginYN", login);
//		return result;
//	}
//	
//	// 사원별 근태 내역 조회
//	@GetMapping("/timecard/list/{user_idx}")
//	public Map<String, Object> attendList(@PathVariable int user_idx, @RequestHeader Map<String, String> header) {
//		result = new HashMap<String, Object>();
//		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
//		List<Map<String, Object>> timecardList = null;
//		
//		if (loginId != null && !loginId.isEmpty()) {
//			timecardList = service.attendList(user_idx);
//			login = true;	
//		}
//		result.put("timecardList", timecardList);
//		result.put("loginYN", login);
//		return result;
//	}
//	
//	// 부서별 근태 내역 조회
//	@GetMapping("/timecard/list/dept/{dept_idx}")
//	public Map<String, Object> attendListDept(@PathVariable int dept_idx, @RequestHeader Map<String, String> header) {
//		result = new HashMap<String, Object>();
//		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
//		List<Map<String, Object>> timecardList = null;
//		
//		if (loginId != null && !loginId.isEmpty()) {
//			timecardList = service.attendListDept(dept_idx);
//			login = true;	
//		}
//		result.put("timecardList", timecardList);
//		result.put("loginYN", login);
//		return result;
//	}
//	
//	// 기간별 근태 내역 조회
//	@GetMapping("/timecard/list/date")
//	public Map<String, Object> attendListDate(@RequestParam String start_date, @RequestParam String end_date,
//	    @RequestHeader Map<String, String> header) {
//	    result = new HashMap<String, Object>();
//	    String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
//
//	    if (loginId != null && !loginId.isEmpty()) {
//	        List<Map<String, Object>> timecardList = service.attendListDate(start_date, end_date);
//	        result.put("timecardList", timecardList);
//	        result.put("loginYN", true);
//	    }
//	    return result;
//	}
//	
//	// 근태 수정
//	@PostMapping("timecard/update")
//	public Map<String, Object> timecardUpdate(@RequestBody TimecardDTO dto,
//			@RequestHeader Map<String, String> header) {
//		result = new HashMap<String, Object>();
//		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
//		
//		if (loginId != null && !loginId.isEmpty()) {
//			success = service.timecardUpdate(dto);
//			result.put("success", success);
//			result.put("loginYN", true);
//		}
//		return result;
//	}

	// 리스트
	@PostMapping(value="/timecard/list")
	public Map<String, Object> timecardList(@RequestBody Map<String, Object>param,@RequestHeader Map<String, String>header){
		log.info("param : {}",param);
		log.info("header : {}",header);
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		
		if(loginId != null && !loginId.isEmpty()) {
			Map<String, Object> result = service.timecardList(param);
			result.put("loginId", loginId);
		}
		return result; 
	}

	@PostMapping(value="/timecard/insert")
	public Map<String, Object>timecardList(@RequestBody TimecardDTO dto,@RequestHeader Map<String, String>header){
		log.info("dto : {}",dto);
		log.info("header : {}",header);
		result = new HashMap<String, Object>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		boolean success = false;
		if(loginId != null && !loginId.isEmpty()) {
			success = service.timecardInsert(dto);
		}
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/timecard/update")
	public Map<String, Object>timecardUpdate(@RequestBody TimecardDTO dto,@RequestHeader Map<String, String>header){
		log.info("dto : {}",dto);
		log.info("header : {}",header);
		result = new HashMap<String, Object>();
		String loginId = (String)Jwt.readToken(header.get("authorization")).get("user_id");
		boolean success = false;
		if(loginId != null && !loginId.isEmpty()) {
			success = service.timecardUpdate(dto);
		}
		result.put("success", success);
		return result;
	}
	

}
