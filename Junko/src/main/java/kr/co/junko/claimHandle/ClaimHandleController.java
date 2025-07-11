package kr.co.junko.claimHandle;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.ClaimHandleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class ClaimHandleController {

	private final ClaimHandleService service;
	Map<String, Object>result =null;
	
	@PostMapping(value="/claimHandle/insert")
	public Map<String, Object>claimHandleInsert(@RequestBody ClaimHandleDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		try {
			boolean success = service.claimHandleInsert(dto);
			result.put("success", success);	
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@PostMapping(value="/claimHandle/update")
	public Map<String, Object>claimHandleUpdate(@RequestBody ClaimHandleDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.claimHandleUpdate(dto);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/claimHandle/list")
	public Map<String, Object>claimHandleList(@RequestBody Map<String, Object> param){
		log.info("param : {}",param);
		return service.claimHandleList(param);
	}
	
	@GetMapping(value="/claimHandle/del/{claim_handle_idx}")
	public Map<String, Object>claimHandleDel(@PathVariable int claim_handle_idx){
		log.info("idx = "+claim_handle_idx);
		result = new HashMap<String, Object>();
		boolean success = service.claimHandleDel(claim_handle_idx);
		result.put("success", success);
		return result;
	}
}
