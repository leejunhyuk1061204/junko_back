package kr.co.junko.order;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.FullOrderDTO;
import kr.co.junko.dto.OrderDTO;
import kr.co.junko.dto.OrderPlanDTO;
import kr.co.junko.dto.OrderProductDTO;
import kr.co.junko.dto.PlanProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class OrderController {
	
	private final OrderService service;
	Map<String, Object>result = null;
	
	// 발주 등록
	@PostMapping(value="/order/insert")
	public Map<String, Object>orderInsert(@RequestBody OrderDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>(); 
		boolean success = service.orderInsert(dto);
		result.put("success", success);
		return result;
	}
	
	// 발주 상품 등록
	@PostMapping(value="/orderProduct/insert")
	public Map<String, Object>orderProductInsert(@RequestBody OrderProductDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>(); 
		boolean success = service.orderProductInsert(dto);
		result.put("success", success);
		return result;
	}
	
	// 발주 계획 등록
	@PostMapping(value="/orderPlan/insert")
	public Map<String, Object>orderPlanInsert(@RequestBody OrderPlanDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.orderPlanInsert(dto);
		result.put("success", success);
		return result;
	}
	
	// 발주 등록 트랜잭션
	@PostMapping(value="/order/full/insert")
	public Map<String, Object>orderFullInsert(@RequestBody FullOrderDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>(); 
		
		try {
			boolean success = service.orderFullInsert(dto);
			result.put("success", success);
		} catch (RuntimeException e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	// 발주 수정
	@PostMapping(value="/order/update")
	public Map<String, Object>orderUpdate(@RequestBody OrderDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		try {
			boolean success = service.orderUpdate(dto);
			result.put("success", success);
		} catch (RuntimeException e) {
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	// 발주 상품 수정
		@PostMapping(value="/orderProduct/update")
		public Map<String, Object>orderProductUpdate(@RequestBody OrderProductDTO dto){
			log.info("dto : {}",dto);
			result = new HashMap<String, Object>(); 
			boolean success = service.orderProductUpdate(dto);
			result.put("success", success);
			return result;
		}
		
		// 발주 계획 수정
		@PostMapping(value="/orderPlan/update")
		public Map<String, Object>orderPlanUpdate(@RequestBody OrderPlanDTO dto){
			log.info("dto : {}",dto);
			result = new HashMap<String, Object>(); 
			boolean success = service.orderPlanUpdate(dto);
			result.put("success", success);
			return result;
		}
		
		// 발주 계획 상품 수정
		@PostMapping(value="/planProduct/update")
		public Map<String, Object>planProductUpdate(@RequestBody PlanProductDTO dto){
			log.info("dto : {}",dto);
			result = new HashMap<String, Object>(); 
			boolean success = service.planProductUpdate(dto);
			result.put("success", success);
			return result;
			
		}
		
		// 발주 리스트
		@PostMapping(value="/order/list")
		public Map<String, Object>orderList(@RequestBody Map<String, Object>param){
			log.info("param : {}",param);
			return service.orderList(param);
		}
		
		// 발주 품목 리스트
		@PostMapping(value="/orderProduct/list")
		public Map<String, Object>orderProductList(@RequestBody Map<String, Object>param){
			log.info("param : {}",param);
			return service.orderProductList(param);
		}
		
		
		// 발주 계획 리스트
		@PostMapping(value="/orderPlan/list")
		public Map<String, Object>orderPlanList(@RequestBody Map<String, Object>param){
			log.info("param : {}",param);
			return service.orderPlanList(param);
		}
		
		// 발주 계획 상품 리스트
		@PostMapping(value="/planProduct/list")
		public Map<String, Object>planProductList(@RequestBody Map<String, Object>param){
			log.info("param : {}",param);
			return service.planProductList(param);
		}
		
		// 번호로 발주정보 가져오기
		@GetMapping(value="/orderByIdx/{order_idx}")
		public Map<String, Object>orderByIdx(@PathVariable int order_idx){
			log.info("idx = " + order_idx);
			result = new HashMap<String, Object>();
			OrderDTO dto = service.orderByIdx(order_idx);
			result.put("data", dto);
			return result;
		}
		
		// 번호로 발주품목 가져오기
		@GetMapping(value="/orderProductByIdx/{order_product_idx}")
		public Map<String, Object>orderProductByIdx(@PathVariable int order_product_idx){
			log.info("idx = " + order_product_idx);
			result = new HashMap<String, Object>();
			OrderProductDTO dto = service.orderProductByIdx(order_product_idx);
			result.put("data", dto);
			return result;
		}
		
		// 번호로 발주계획 가져오기
		@GetMapping(value="/orderPlanByIdx/{plan_idx}")
		public Map<String, Object>orderPlanByIdx(@PathVariable int plan_idx){
			log.info("idx = " + plan_idx);
			result = new HashMap<String, Object>();
			OrderPlanDTO dto = service.orderPlanByIdx(plan_idx);
			result.put("data", dto);
			return result;
		}
		
		// 발주 삭제
		@GetMapping(value="/order/del/{order_idx}")
		public Map<String, Object>orderDel(@PathVariable int order_idx){
			log.info("idx : "+order_idx);
			result = new HashMap<String, Object>();
			boolean success = service.orderDel(order_idx);
			result.put("success", success);
			return result;
		}
		
		// 발주 물품 삭제
		@GetMapping(value="/orderProduct/del/{order_product_idx}")
		public Map<String, Object>orderProductDel(@PathVariable int order_product_idx){
			log.info("idx : "+order_product_idx);
			result = new HashMap<String, Object>();
			boolean success = service.orderProductDel(order_product_idx);
			result.put("success", success);
			return result;
		}
		
		// 발주 계획 삭제
		@GetMapping(value="/orderPlan/del/{plan_idx}")
		public Map<String, Object>orderPlanDel(@PathVariable int plan_idx){
			log.info("idx : "+plan_idx);
			result = new HashMap<String, Object>();
			boolean success = service.orderPlanDel(plan_idx);
			result.put("success", success);
			return result;
		}
				
		// 발주 삭제 트랜잭션
		@GetMapping(value="/order/full/del/{order_idx}")
		public Map<String, Object>orderFullDel(@PathVariable int order_idx){
			log.info("idx : "+order_idx);
			result = new HashMap<String, Object>();
			try {
				boolean success = service.orderFullDel(order_idx);
				result.put("success", success);
			} catch (RuntimeException e) {
				e.printStackTrace();
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
			return result;
		}
		
	
	
	

}
