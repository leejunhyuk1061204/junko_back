package kr.co.junko.chart;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChartController {
	
	private final ChartService service;
	
	@PostMapping("/list/chart")
	public Map<String, Object> chart() {
		return service.chart();
	}

}
