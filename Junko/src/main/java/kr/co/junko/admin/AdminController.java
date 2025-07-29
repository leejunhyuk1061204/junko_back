package kr.co.junko.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.PowerDTO;
import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {
	
	private final AdminService service;
	Map<String, Object> result = null;

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
	
	// 사원 정보 수정
	@PostMapping("/emp/update")
	public Map<String, Object> empUpdate(@RequestBody Map<String, Object> param,
			@RequestHeader Map<String, String> header) {
		result = new HashMap<String, Object>();
		boolean success = false;
		boolean login = false;
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		
		if (loginId != null && !loginId.isEmpty()) {
			success = service.empUpdate(param);
			login = true;
		}
		result.put("success", success);
		result.put("loginYN", login);
		return result;
	}
	
	// 조직도 조회
	@GetMapping("/orgchart/tree")
	public Map<String, Object> deptTree(@RequestHeader Map<String, String> header) {
		result = new HashMap<String, Object>();
		boolean login = false;
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		
		if (loginId != null && !loginId.isEmpty()) {
			List<Map<String, Object>> tree = service.deptTree();
			List<Map<String, Object>> userList = service.allUserList(); 
			result.put("deptTree", tree);
			result.put("userList", userList);
			login = true;
		}
		result.put("loginYN", login);
		return result;
	}
	
	// 부서별 직원 리스트 조회
	@PostMapping("/user/list")
	public Map<String, Object> userList(
			@RequestParam(required = false, defaultValue = "") String dept_name,
	        @RequestParam(required = false, defaultValue = "") String search,
	        @RequestParam(required = false, defaultValue = "1") int page,
	        @RequestParam(required = false, defaultValue = "10") int size,
	        @RequestParam(required = false, defaultValue = "hire_date ASC")String sort) {
		
		
		Map<String, Object> param = new HashMap<>();
	    param.put("dept_name", dept_name.trim());
	    param.put("search", search.trim());
	    param.put("page", page);
	    param.put("size", size);
	    param.put("sort", sort.trim());

	    log.info("--------------------------------------------받은 param: " + param);
		return service.userList(param);
	}
	
	// 직원 상세보기
	@GetMapping("/user/detail/{user_idx}")
	public Map<String, Object> userDetail(@PathVariable int user_idx, @RequestHeader Map<String, String> header) {
	    result = new HashMap<String, Object>();
	    boolean login = false;
	    Map<String, Object> userDetail = null;
	    String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

	    if (loginId != null && !loginId.isEmpty()) {
	    	userDetail = service.userDetail(user_idx);
	        login = true;
	    }
	    result.put("userDetail", userDetail);
	    result.put("loginYN", login);
	    return result;
	}
	
	// 퇴사 인 애들 권한 다 뺏기
	@GetMapping("/admin/revoke/grant")
	public Map<String, Object> revokeGrant(@RequestHeader Map<String, String> header){
	    result = new HashMap<String, Object>();
	    boolean login = false;
	    Map<String, Object> userDetail = null;
	    String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
	    int cnt = 0;
	    
	    if (loginId != null && !loginId.isEmpty()) {
	    	cnt = service.revokeGrant();
	    	login = true;
	    }
	    
	    result.put("updated_cnt", cnt);
	    result.put("success", true);
	    
		return result;
	}
	
	@GetMapping("/dept/list")
	public Map<String, Object> getDeptList(@RequestHeader Map<String, String> header) {
	    result = new HashMap<String, Object>();
	    boolean login = false;
	    String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

	    if (loginId != null && !loginId.isEmpty()) {
	        List<Map<String, Object>> list = service.getDeptList();
	        result.put("list", list);
	        login = true;
	    }

	    result.put("loginYN", login);
	    return result;
	}
	
	@GetMapping("/job/list")
	public List<Map<String, Object>> jobList() {
		return service.getJobList();
	}
	
	@GetMapping("/status/list")
	public List<Map<String, Object>> statusList() {
		return service.getStatusList();
	}
	
}
