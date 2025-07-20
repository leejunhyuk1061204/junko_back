package kr.co.junko.shipment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.ShipmentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class ShipmentController {

	private final ShipmentService service;
	Map<String, Object> result = null;
	
	@PostMapping(value="/shipment/update")
	public Map<String, Object> shipmentUpdate(@RequestBody ShipmentDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		try {
			boolean success = service.shipmentUpdate(dto);
			result.put("success", success);	
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@PostMapping(value="/shipment/list")
	public Map<String, Object>shipmentList(@RequestBody Map<String, Object> param){
		log.info("param : {}",param);
		return service.shipmentList(param);
	}
	
	@GetMapping(value="/shipment/del/{shipment_idx}")
	public Map<String, Object>shipmentDel(@PathVariable int shipment_idx){
		log.info("idx : "+shipment_idx);
		result = new HashMap<String, Object>();
		boolean success = service.shipmentDel(shipment_idx);
		result.put("success", success);
		return result;
	}
	
	// 출고할 상품 리스트
	@GetMapping(value="/shipmentProduct/list/{shipment_idx}")
	public Map<String, Object>shipmentProductList(@PathVariable int shipment_idx){
		log.info("idx: "+shipment_idx);
		return service.shipmentProductList(shipment_idx);
	}
	
	// 출고할 상품의 보관 위치 리스트
	@PostMapping(value="/shipmentProductStockList")
	public Map<String, Object>shipmentProductStockList(@RequestBody Map<String, Object>param){
		log.info("param: {}",param);
		return service.shipmentProductStockList(param);
	}
	
}
