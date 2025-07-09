package kr.co.junko.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.MemberDTO;
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
	@PostMapping("/JobNdept/update/")
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

}
