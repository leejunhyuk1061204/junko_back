package kr.co.junko.order;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.custom.CustomDAO;
import kr.co.junko.document.DocumentService;
import kr.co.junko.dto.DocumentCreateDTO;
import kr.co.junko.dto.FullOrderDTO;
import kr.co.junko.dto.OrderDTO;
import kr.co.junko.dto.OrderPlanDTO;
import kr.co.junko.dto.OrderProductDTO;
import kr.co.junko.dto.PlanProductDTO;
import kr.co.junko.dto.ProductDTO;
import kr.co.junko.product.ProductDAO;
import kr.co.junko.receive.ReceiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
	
	private final OrderDAO dao;
	private final ReceiveService ReceiveService;
	private final DocumentService documentService;
	private final ProductDAO productDAO;
	private final CustomDAO customDAO;

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
	
	public boolean planProductInsert(PlanProductDTO dto) {
		int row = dao.planProductInsert(dto);
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
	
	public boolean planProductUpdate(PlanProductDTO dto) {
		return dao.planProductUpdate(dto)>0;
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
	
	public Map<String, Object> planProductList(Map<String, Object> param) {
		int cnt = 5;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<PlanProductDTO>list = dao.planProductList(param);
		int total = dao.planProductListTotalPage(param);
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
		
		Map<String, OrderProductDTO> productMap = new HashMap<String, OrderProductDTO>();
		
		boolean orderResult = orderInsert(dto.getOrder());
		if(!orderResult) throw new RuntimeException("발주 등록 실패");
		
		for (OrderProductDTO product : dto.getOrderProduct()) {
			product.setOrder_idx(dto.getOrder().getOrder_idx());
			boolean orderProductResult = orderProductInsert(product);
			if(!orderProductResult) throw new RuntimeException("발주 상품 등록 실패");
			productMap.put(product.getTempId(), product);
		}
		
		for (OrderPlanDTO plan : dto.getOrderPlan()) {
			plan.setOrder_idx(dto.getOrder().getOrder_idx());
			boolean orderPlanResult = orderPlanInsert(plan);
			if(!orderPlanResult) throw new RuntimeException("발주 계획 등록 실패");
			
			for (PlanProductDTO pp : plan.getPlanProduct()) {
				pp.setPlan_idx(plan.getPlan_idx());
				pp.setOrder_product_idx(productMap.get(pp.getProductTempId()).getOrder_product_idx());
				boolean ppResult = planProductInsert(pp);
				if(!ppResult) throw new RuntimeException("발주 계획 상품 등록 실패");
			}
		}
		
		// 발주서 생성
		// order : 6개 order_idx, custom_name, reg_date, user_id, orderProduct, orderPlan
		// orderProduct : 4개 product_idx, 	product_name, product_option_idx, order_cnt
		// orderPlan : 5개 delivery_date, product_idx, 	product_name, product_option_idx, order_cnt
		
		String order ="";
		String orderProduct="";
		String orderPlan="";
		Map<String, String>variables = null;
		
		//orderPlan
		for(OrderPlanDTO plan : dto.getOrderPlan()) {
			for(PlanProductDTO pp : plan.getPlanProduct()) {
				variables = new HashMap<String, String>();
				variables.put("delivery_date", plan.getDelivery_date().toString());
				variables.put("product_idx", String.valueOf(productMap.get(pp.getProductTempId()).getProduct_idx()));
				variables.put("product_name", productDAO.selectProductIdx(productMap.get(pp.getProductTempId()).getProduct_idx()).getProduct_name());
				variables.put("product_option_idx", String.valueOf(productMap.get(pp.getProductTempId()).getProduct_option_idx()));
				variables.put("order_cnt", String.valueOf(pp.getOrder_cnt()));
				
				orderPlan += documentService.documentPreview(8, variables);
			}
		}
		
		//orderProduct
		for(OrderProductDTO product : dto.getOrderProduct()) {
			variables = new HashMap<String, String>();
			variables.put("product_idx", String.valueOf(product.getProduct_idx()));
			variables.put("product_name", productDAO.selectProductIdx(product.getProduct_idx()).getProduct_name());
			variables.put("product_option_idx", String.valueOf(product.getOrder_product_idx()));
			variables.put("order_cnt", String.valueOf(product.getOrder_cnt()));
			
			orderProduct += documentService.documentPreview(7, variables);
		}
		
		//order
		variables = new HashedMap<String, String>();
		variables.put("order_idx", String.valueOf(dto.getOrder().getOrder_idx()));
		variables.put("custom_name", customDAO.customSelect(dto.getOrder().getCustom_idx()).getCustom_name());
		variables.put("reg_date", LocalDate.now().toString());
		variables.put("user_id", String.valueOf(dto.getOrder().getUser_idx()));
		variables.put("orderProduct", orderProduct);
		variables.put("orderPlan", orderPlan);
		
		DocumentCreateDTO documentCreateDTO = new DocumentCreateDTO();
		documentCreateDTO.setTemplate_idx(6);
		documentCreateDTO.setUser_idx(dto.getOrder().getUser_idx());
		documentCreateDTO.setVariables(variables);
		Map<String, Object> documentResult = documentService.documentInsert(documentCreateDTO);
		if(!(boolean)documentResult.get("success")) throw new RuntimeException("문서 등록 실패");
		
		
		
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
