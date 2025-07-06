package kr.co.junko.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.FullOrderDTO;
import kr.co.junko.dto.OrderDTO;
import kr.co.junko.dto.OrderPlanDTO;
import kr.co.junko.dto.OrderProductDTO;
import kr.co.junko.receive.ReceiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
	
	private final OrderDAO dao;
	private final ReceiveService ReceiveService;

	public boolean orderInsert(OrderDTO dto) {
		int row = dao.orderInsert(dto);
		return row >0;
	}

	public boolean orderProductInsert(OrderProductDTO dto) {
		int row = dao.orderProductInsert(dto);
		return row>0;
	}

	public boolean orderPlanInsert(OrderPlanDTO dto) {
		int row = dao.orderPlanInsert(dto);
		return row>0;
	}

	public boolean orderUpdate(OrderDTO dto) {
		if("확정".equals(dto.getStatus())) {
			return ReceiveService.fullInsertReceive(dto);
		} else {
			return dao.orderUpdate(dto)>0;
		}
	}

	public boolean orderProductUpdate(OrderProductDTO dto) {
		int row = dao.orderProductUpdate(dto);
		return row>0;
	}

	public boolean orderPlanUpdate(OrderPlanDTO dto) {
		int row = dao.orderPlanUpdate(dto);
		return row>0;
	}

	public Map<String, Object> orderList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<OrderDTO>list = dao.orderList(param);
		int total = dao.orderTotalPage(param);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("orederList", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public Map<String, Object> orderProductList(Map<String, Object> param) {
		int cnt = 5;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<OrderProductDTO>list = dao.orderProductList(param);
		int total = dao.orderProductTotalPage(param);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("orederProductList", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public Map<String, Object> orderPlanList(Map<String, Object> param) {
		int cnt = 5;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<OrderPlanDTO>list = dao.orderPlanList(param);
		int total = dao.orderPlanListTotalPage(param);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("orederPlanList", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public OrderDTO orderByIdx(int idx) {
		return dao.orderByIdx(idx);
	}

	public OrderProductDTO orderProductByIdx(int idx) {
		return dao.orderProductByIdx(idx);
	}

	public OrderPlanDTO orderPlanByIdx(int idx) {
		return dao.orderPlanByIdx(idx);
	}

	public boolean orderDel(int idx) {
		int row = dao.orderDel(idx);
		return row>0;
	}

	public boolean orderProductDel(int idx) {
		int row = dao.orderProductDel(idx);
		return row>0;
	}

	public boolean orderPlanDel(int idx) {
		int row = dao.orderPlanDel(idx);
		return row>0;
	}

	@Transactional
	public boolean orderFullInsert(FullOrderDTO dto) {
		
		boolean orderResult = orderInsert(dto.getOrder());
		if(!orderResult) throw new RuntimeException("발주 등록 실패");
		boolean orderProductResult = orderProductInsert(dto.getOrderProduct());
		if(!orderProductResult) throw new RuntimeException("발주 상품 등록 실패");
		boolean orderPlanResult = orderPlanInsert(dto.getOrderPlan());
		if(!orderPlanResult) throw new RuntimeException("발주 계획 등록 실패");
		
		return true;
	}

	@Transactional
	public boolean orderFullDel(int idx) {
		boolean orderResult = dao.orderDel(idx)>0;
		if(!orderResult) throw new RuntimeException("발주 삭제 실패");
		boolean orderProductResult = dao.orderProductDelByOrderIdx(idx)>0;
		if(!orderProductResult) throw new RuntimeException("발주 상품 삭제 실패");
		boolean orderPlanResult = dao.orderPlanDelByOrderIdx(idx)>0;
		if(!orderPlanResult) throw new RuntimeException("발주 계획 삭제 실패");
		return true;
	}

}
