package kr.co.junko.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.PowerDTO;
import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminController {
	
	private final AdminService service;
	Map<String, Object> result = null;
	
	// 직책, 부서 등록 & 수정
	@PostMapping("/JobNdept/update")
	public Map<String, Object> updateJobNdept(@RequestBody MemberDTO dto,
			@RequestHeader Map<String, String> header) {
		log.info("dto : {}", dto);
		boolean success = false;
		boolean login = false;
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		result = new HashMap<String, Object>();
		
		if (loginId != null && !loginId.isEmpty()) {
			success = service.updateJobNdept(dto);
			login = true;
			result.put("dept_idx", dto.getDept_idx());
			result.put("job_idx", dto.getJob_idx());
		}		
		result.put("success", success);
		result.put("loginYN", login);
		return result;
	}

	// 권한 등록
	@PostMapping("/power/insert")
	public Map<String, Object> insertPower(
			@RequestBody Map<String, Object> param,
			@RequestHeader Map<String, String> header){
		boolean success = false;
		boolean login = false;
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		
		result = new HashMap<String, Object>();
		String power_name = (String) param.get("power_name");
		
		if (loginId != null && !loginId.isEmpty()) {
			success = service.insertPower(param);
			login = true;
		}
		
		result.put("success", success);
		result.put("loginYN", login);
		
		return result;
	}
	
	// 권한 부여
	@PostMapping("/power/grant")
	public Map<String, Object> grantPower(
			@RequestBody Map<String, Integer>param,
			@RequestHeader Map<String, String> header){
		boolean success = false;
		boolean login = false;
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		
		result = new HashMap<String, Object>();
		
		int user_idx = param.get("user_idx");
		int power_idx = param.get("power_idx");

		if (loginId != null && !loginId.isEmpty()) {
			success = service.grantPower(user_idx, power_idx);
			login = true;
		}
		
		result.put("success", success);
		result.put("loginYN", login);
		
		return result;
	}
	
	// 권한 목록
	@GetMapping("/power/list/{user_idx}")
	public Map<String, Object> powerList(@PathVariable int user_idx){
		result = new HashMap<String, Object>();
		
		List<PowerDTO> list = service.powerList(user_idx);
		result.put("list", list);
		
		return result;
	}
	
	// 권한 일괄 수정
	@PostMapping("/power/updateAll")
	public Map<String, Object> updatePowerAll(
			@RequestBody Map<String, Object>param,
			@RequestHeader Map<String, String> header){
		boolean success = false;
		boolean login = false;
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		
		result = new HashMap<String, Object>();
		
		int user_idx = (int) param.get("user_idx");
		List<Integer> power_list = (List<Integer>) param.get("power_list");
		
		if (loginId != null && !loginId.isEmpty()) {
			success = service.updatePowerAll(user_idx, power_list);
			login = true;
		}
		
		result.put("success", success);
		result.put("loginYN", login);
		
		return result;
	}
	
	// 특정 권한 가진 사용자 목록 조회
	@GetMapping("/power/users/{power_idx}")
	public Map<String, Object> getUserPower(@PathVariable int power_idx){
		result = new HashMap<String, Object>();
		
		List<MemberDTO> users = service.getUserPower(power_idx);
		result.put("users", users);
		
		return result;
	}
	
	// 권한 수정
	@PutMapping("/power/update")
	public Map<String, Object> updatePower(
			@RequestBody Map<String, Object>param,
			@RequestHeader Map<String, String> header){
		boolean success = false;
		boolean login = false;
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
	
		result = new HashMap<String, Object>();
		
		String power_name = (String) param.get("power_name");
		int power_idx = (int) param.get("power_idx");
		
		if (loginId != null && !loginId.isEmpty()) {
			success = service.updatePower(power_idx, power_name);
			login = true;
		}
		
		result.put("success", success);
		result.put("loginYN", login);
		
		return result;
	}
	
	// 권한 삭제
	@PutMapping("/power/del/{power_idx}")
	public Map<String, Object> delPower(
			@PathVariable int power_idx,
			@RequestHeader Map<String, String> header){
		boolean success = false;
		boolean login = false;
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
	
		result = new HashMap<String, Object>();
		
		if (loginId != null && !loginId.isEmpty()) {
			success = service.delPower(power_idx);
			login = true;
		}

		result.put("success", success);
		result.put("loginYN", login);
		
		return result;
	}
	
	// 사원 퇴사 처리
	@PostMapping("/resign/update")
	public Map<String, Object> resignUpdate(@RequestBody Map<String, Object> param,
			@RequestHeader Map<String, String> header) {
		log.info("퇴사 처리 : {}",param);
		result = new HashMap<String, Object>();
		boolean success = false;
		boolean login = false;
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		
		if (loginId != null && !loginId.isEmpty()) {
			success = service.resignUpdate(param);
			login = true;
		}
		result.put("success", success);
		result.put("loginYN", login);
		return result;
	}
	
}
