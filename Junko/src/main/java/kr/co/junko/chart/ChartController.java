package kr.co.junko.chart;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
@CrossOrigin
public class ChartController {
	
	private final ChartService service;
	
	// 차트 불러오기
	@PostMapping("/list/chart")
	public Map<String, Object> chart(@RequestBody Map<String, Object> param,
			@RequestHeader Map<String, String> header) {
		log.info("param : {}",param);
		Map<String, Object> result = new HashMap<String, Object>();
		Integer categoryIdx = (Integer) param.get("categoryIdx");
		String startDate = (String) param.get("startDate");
		String endDate = (String) param.get("endDate");
		
		if(categoryIdx != null) {
			param.put("categoryIdx", categoryIdx);
		}
		if (startDate != null && endDate != null) {
	        param.put("startDate", startDate);
	        param.put("endDate", endDate);
	    }
		Map<String, Object> chartData = service.chart(param);
		result.put("success", true);
		result.put("chartData", chartData);
		
		return result;
	}
	
	// 엑셀 다운로드
	@GetMapping("/chart/excel")
	public void chartExcel(@RequestParam Map<String, Object> param,
			HttpServletResponse res, @RequestHeader Map<String, String> header) throws IOException {
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		Integer categoryIdx = param.get("categoryIdx") != null ? Integer.parseInt(param.get("categoryIdx").toString()) : null;
		String startDate = (String) param.get("startDate");
		String endDate = (String) param.get("endDate");
		
		if (loginId != null && !loginId.isEmpty()) {
			if(categoryIdx != null) {
				param.put("categoryIdx", categoryIdx);
			}
			if (startDate != null && endDate != null) {
		        param.put("startDate", startDate);
		        param.put("endDate", endDate);
		    }else {
		    	res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		    	return;		    	
		    }
		}
	    service.chartExcel(param, res); // 로그인 성공시
	}

	// PDF 다운로드
	@GetMapping("/chart/pdf")
	public void chartPdf(@RequestParam Map<String, Object> param,
			HttpServletResponse res, @RequestHeader Map<String, String> header) throws IOException {
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		Integer categoryIdx = (Integer) param.get("categoryIdx");
		String startDate = (String) param.get("startDate");
		String endDate = (String) param.get("endDate");
		
		if (loginId != null && !loginId.isEmpty()) {
			if(categoryIdx != null) {
				param.put("categoryIdx", categoryIdx);
			}
			if (startDate != null && endDate != null) {
		        param.put("startDate", startDate);
		        param.put("endDate", endDate);
		    }else {
		    	res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		    	return;		    	
		    }
		}
		service.chartPdf(param, res);
	}

}
