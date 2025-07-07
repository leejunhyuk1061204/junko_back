package kr.co.junko.chart;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChartController {
	
	private final ChartService service;
	
	@PostMapping("/list/chart")
	public Map<String, Object> chart(@RequestBody Map<String, Object> param,
			@RequestParam Integer categoryIdx,
			@RequestParam String startDate,
			@RequestParam String endDate,
			@RequestHeader Map<String, String> header) {
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		Map<String, Object> result = new HashMap<String, Object>();
		
		if (loginId != null && !loginId.isEmpty()) {
			if(categoryIdx != null) {
				param.put("categoryIdx", categoryIdx);
			}
			if (startDate != null && endDate != null) {
		        param.put("startDate", startDate);
		        param.put("endDate", endDate);
		    }
			
			Map<String, Object> chartData = service.chart(param);
			result.put("success", true);
			result.put("loginYN", true);
			result.put("chartData", chartData);
		}else {
			result.put("success", false);
			result.put("loginYN", false);
		}
		return result;
	}

}
