package kr.co.junko.adminLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public Map<String, Object> logList(@RequestParam Map<String, Object> param,
			@RequestHeader Map<String, String> header) {
		Map<String, Object> result = new HashMap<String, Object>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		boolean login = false;
		
		if (loginId != null && !loginId.isEmpty()) {
			List<Map<String, Object>> list = service.logList(param);
			login = true;
			result.put("startDate", param.get("startDate"));
			result.put("endDate", param.get("endDate"));
			result.put("list", list);
			result.put("loginYN", login);
		}
		return result;
	}
	
	

}