package kr.co.junko.chart;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.category.CategoryService;
import kr.co.junko.dto.CategoryDTO;
import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class ChartController {
	
	private final ChartService service;
	private final CategoryService cateService;
	
	// 차트 불러오기
	@PostMapping("/list/chart")
	public Map<String, Object> chart(@RequestBody Map<String, Object> param,
			@RequestHeader Map<String, String> header) {
		log.info("param : {}",param);
		Map<String, Object> result = new HashMap<String, Object>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		Integer categoryIdx = (Integer) param.get("categoryIdx");
		String startDate = (String) param.get("startDate");
		String endDate = (String) param.get("endDate");
		boolean login = false;
		
		if (loginId != null && !loginId.isEmpty()) {
			if(categoryIdx != null) {
				param.put("categoryIdx", categoryIdx);
			}
			if (startDate != null && endDate != null) {
		        param.put("startDate", startDate);
		        param.put("endDate", endDate);
		    }
			Map<String, Object> chartData = service.chart(param);
			login = true;
			result.put("success", true);
			result.put("chartData", chartData);
			result.put("loginYN", login);
		}
		return result;
	}
	
	// 엑셀 다운로드
	@GetMapping("/chart/excel")
	public void chartExcel(@RequestParam Map<String, Object> param,
			HttpServletResponse res, @RequestHeader Map<String, String> header) throws IOException {
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		Integer categoryIdx = param.get("categoryIdx") != null ? Integer.parseInt(param.get("categoryIdx").toString()) : null;
		
		if (loginId != null && !loginId.isEmpty()) {
			if(categoryIdx != null) {
				param.put("categoryIdx", categoryIdx);
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
		
		if (loginId != null && !loginId.isEmpty()) {
			if(categoryIdx != null) {
				param.put("categoryIdx", categoryIdx);
			}
		}
		service.chartPdf(param, res);
	}
	
	// 차트용 카테고리 분류
	@GetMapping("/chart/category/list")
	public Map<String, Object> chartCategoryList(@RequestHeader Map<String, String> header) {
		Map<String, Object> result = new HashMap<String, Object>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
		boolean login = false;
		
		if (loginId != null && !loginId.isEmpty()) {
			List<CategoryDTO> all = cateService.cateList();
			List<CategoryDTO> parents = all.stream().filter(c -> c.getCategory_parent() == null || c.getCategory_parent() == 0)
					.collect(Collectors.toList());
			List<CategoryDTO> subs = all.stream().filter(c -> c.getCategory_parent() != null && c.getCategory_parent() > 0)
					.collect(Collectors.toList());
			login = true;
			
			result.put("parents", parents);
			result.put("subs", subs);
			result.put("loginYN", login);
		}
		return result;
	}

}
