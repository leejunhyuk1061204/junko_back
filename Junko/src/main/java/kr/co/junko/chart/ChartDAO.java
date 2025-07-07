package kr.co.junko.chart;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChartDAO {

	List<Map<String, Object>> getDaySales(Map<String, Object> param);

	List<Map<String, Object>> getRecentOrderStats(Map<String, Object> param);

	List<Map<String, Object>> getPopularProduct(Map<String, Object> param);

	List<Map<String, Object>> getHighReturnProduct(Map<String, Object> param);

	List<Map<String, Object>> getInventoryTurnoverStats(Map<String, Object> param);
	
	List<Map<String, Object>> getMonthlySalesYoY(Map<String, Object> param);


}
