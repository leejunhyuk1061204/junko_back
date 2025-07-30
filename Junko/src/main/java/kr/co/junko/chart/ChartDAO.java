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
	
	List<Map<String, Object>> returnProduct(Map<String, Object> param);
	
	List<Map<String, Object>> returnProductThisMonth(Map<String, Object> param);
	
	List<Map<String, Object>> getDelayedProduct(Map<String, Object> param);
	
	List<Map<String, Object>> getOrderStatus(Map<String, Object> param);

	List<Map<String, Object>> getProductMarginStats(Map<String, Object> param);

	List<Map<String, Object>> getNetProfitStats(Map<String, Object> param);

	List<Map<String, Object>> getInOutProduct(Map<String, Object> param);

	List<Map<String, Object>> getMonthlySalesYoY(Map<String, Object> param);

	List<Map<String, Object>> getLowStockProduct(Map<String, Object> param);

	List<Map<String, Object>> getSalesThisMonth(Map<String, Object> param);

	List<Map<String, Object>> newOrder(Map<String, Object> param);

	List<Map<String, Object>> getPendingShipment(Map<String, Object> param);

	List<Map<String, Object>> getShippedToday(Map<String, Object> param);

	List<Map<String, Object>> getReceiveThisMonth(Map<String, Object> param);

	List<Map<String, Object>> getSalesByCategory(Map<String, Object> param);

	List<Map<String, Object>> getSalesByProduct(Map<String, Object> param);


}
