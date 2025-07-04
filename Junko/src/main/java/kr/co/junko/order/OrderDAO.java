package kr.co.junko.order;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.OrderDTO;
import kr.co.junko.dto.OrderPlanDTO;
import kr.co.junko.dto.OrderProductDTO;

@Mapper
public interface OrderDAO {

	int orderInsert(OrderDTO dto);

	int orderProductInsert(OrderProductDTO dto);

	int orderPlanInsert(OrderPlanDTO dto);

	int orderUpdate(OrderDTO dto);

	int orderProductUpdate(OrderProductDTO dto);

	int orderPlanUpdate(OrderPlanDTO dto);

	int orderTotalPage(Map<String, Object> param);

	List<OrderDTO> orderList(Map<String, Object> param);

	int orderProductTotalPage(Map<String, Object> param);

	List<OrderProductDTO> orderProductList(Map<String, Object> param);

	int orderPlanListTotalPage(Map<String, Object> param);

	List<OrderPlanDTO> orderPlanList(Map<String, Object> param);

	OrderDTO orderByIdx(int idx);

	OrderProductDTO orderProductByIdx(int idx);

	OrderPlanDTO orderPlanByIdx(int idx);

	int orderDel(int idx);

	int orderProductDel(int idx);

	int orderPlanDel(int idx);

}
