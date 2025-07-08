package kr.co.junko.claim;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.ClaimDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ClaimController {

	private final ClaimService service;
	Map<String, Object>result = null;
	
	@PostMapping(value="/claim/insert")
	public Map<String, Object>claimInsert(@RequestBody ClaimDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.claimInsert(dto);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/claim/update")
	public Map<String, Object>claimUpdate(@RequestBody ClaimDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.claimUpdate(dto);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/claim/list")
	public Map<String, Object>claimList(@RequestBody Map<String, Object> param){
		log.info("param : {}",param);
		return service.claimList(param);
	}
	
	@GetMapping(value="/claim/del/{claim_idx}")
	public Map<String, Object>claimDel(@PathVariable int claim_idx){
		log.info("idx = "+ claim_idx);
		result= new HashMap<String, Object>();
		boolean success = service.claimDel(claim_idx);
		result.put("success", success);
		return result;
	}
	
}
