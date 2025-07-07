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
	
	public Map<String, Object> chart(Map<String, Object> param) {
		Map<String, Object> list = new HashMap<String, Object>();
		
		list.put("getDaySales", safeList(dao.getDaySales(param)));
		list.put("getRecentOrderStats", safeList(dao.getRecentOrderStats(param)));
		list.put("getPopularProduct", safeList(dao.getPopularProduct(param)));
		list.put("getHighReturnProduct", safeList(dao.getHighReturnProduct(param)));
		list.put("getInventoryTurnoverStats", safeList(dao.getInventoryTurnoverStats(param)));
		list.put("returnProduct", safeList(dao.returnProduct(param)));
		list.put("getDelayedProduct", safeList(dao.getDelayedProduct(param)));
		list.put("getOrderStatus", safeList(dao.getOrderStatus(param)));
		list.put("getMonthlySalesYoY", safeList(dao.getMonthlySalesYoY(param)));
		
		return list;
	}
	
	// NPE 에러 방지 함수
	private List<Map<String, Object>> safeList(List<Map<String, Object>> list) {
	    return (list != null) ? list : new ArrayList<>();
	}

}
