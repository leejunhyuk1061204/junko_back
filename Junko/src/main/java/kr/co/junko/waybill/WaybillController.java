package kr.co.junko.waybill;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.ReturnWaybillDTO;
import kr.co.junko.dto.WaybillDTO;
import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class WaybillController {

	private final WaybillService service;
	Map<String, Object>result = null;
	
	@PostMapping(value="/waybill/insert")
	public Map<String, Object> waybillInsert(@RequestBody WaybillDTO dto,@RequestHeader Map<String, String>header){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		String token = header.get("authorization");
		Map<String, Object>payload = Jwt.readToken(token);
		String loginId = (String)payload.get("user_id");
		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;
		if(login) {
			try {
				success = service.waybillInsert(dto);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("msg", e.getMessage());
			}
		}
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
	
	@PostMapping(value="/returnWaybill/update")
	public Map<String, Object>returnWaybillUpdate(@RequestBody ReturnWaybillDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.returnWaybillUpdate(dto);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/returnWaybill/list")
	public Map<String, Object>returnWaybillList(@RequestBody Map<String, Object> param){
		log.info("param : {}",param);
		return service.returnWaybillList(param);
	}
	
	@GetMapping(value="/returnWaybill/del/{return_waybill_idx}")
	public Map<String, Object>returnWaybillDel(@PathVariable int return_waybill_idx){
		log.info("idx = "+return_waybill_idx);
		result = new HashMap<String, Object>();
		boolean success = service.returnWaybillDel(return_waybill_idx);
		result.put("success", success);
		return result;
	}
	
}
