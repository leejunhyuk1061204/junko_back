package kr.co.junko.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChartService {
	
	private final ChartDAO dao;
	
	public Map<String, Object> chart() {
		Map<String, Object> list = new HashMap<String, Object>();
		
		list.put("getDaySales", safeList(dao.getDaySales()));
		list.put("getRecentOrderStats", safeList(dao.getRecentOrderStats()));
		list.put("getPopularProduct", safeList(dao.getPopularProduct()));
		list.put("getHighReturnProduct", safeList(dao.getHighReturnProduct()));
		list.put("getMonthlySalesYoY", safeList(dao.getMonthlySalesYoY()));
		
		return list;
	}
	
	// NPE 에러 방지 함수
	private List<Map<String, Object>> safeList(List<Map<String, Object>> list) {
	    return (list != null) ? list : new ArrayList<>();
	}

}
