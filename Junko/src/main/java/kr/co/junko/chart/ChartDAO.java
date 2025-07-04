package kr.co.junko.chart;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChartDAO {

	List<Map<String, Object>> getDaySales();

	List<Map<String, Object>> getRecentOrderStats();
	
	List<Map<String, Object>> getPopularProduct();
	
	List<Map<String, Object>> getHighReturnProduct();

	List<Map<String, Object>> getMonthlySalesYoY();

}
