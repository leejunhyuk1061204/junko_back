package kr.co.junko.waybill;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.WaybillDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class WaybillController {

	private final WaybillService service;
	Map<String, Object>result = null;
	
	@PostMapping(value="/waybill/insert")
	public Map<String, Object> waybillInsert(@RequestBody WaybillDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.waybillInsert(dto);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/waybill/update")
	public Map<String, Object>waybillUpdate(@RequestBody WaybillDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.waybillUpdate(dto);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/waybill/list")
	public Map<String, Object>waybillList(@RequestBody Map<String, Object> param){
		log.info("param : {}",param);
		return service.waybillList(param);
	}
	
	@GetMapping(value="/waybill/del/{waybill_idx}")
	public Map<String, Object>waybillDel(@PathVariable int waybill_idx){
		log.info("idx = "+waybill_idx);
		result = new HashMap<String, Object>();
		boolean success = service.waybillDel(waybill_idx);
		result.put("success", success);
		return result;
	}
	
}
