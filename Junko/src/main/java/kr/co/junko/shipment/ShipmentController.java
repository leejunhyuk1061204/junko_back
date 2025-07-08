package kr.co.junko.shipment;

import java.util.HashMap;
import java.util.Map;

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
public class ShipmentController {

	private final ShipmentService service;
	Map<String, Object> result = null;
	
	@PostMapping(value="/shipment/update")
	public Map<String, Object> shipmentUpdate(@RequestBody ShipmentDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.shipmentUpdate(dto);
		result.put("success", success);
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
	
}
