package kr.co.junko.adminLog;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.AdminLogDTO;
import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminLogController {
	
	private final AdminLogService service;
	
	// 로그 리스트 불러오기
	@GetMapping("/admin/logs/list")
	public List<AdminLogDTO> LogList(@RequestParam Map<String, Object> param,
			@RequestHeader Map<String, String> header) {
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		String log_type = (String) param.get("log_type");
		String target_table = (String) param.get("target_table");
		
		if (loginId != null && !loginId.isEmpty()) {
			
		}
		
		
		
		return null;
	}
	
	

}