package kr.co.junko.claim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.ClaimDTO;
import kr.co.junko.dto.FullClaimDTO;
import kr.co.junko.dto.ReturnProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class ClaimController {

	private final ClaimService service;
	Map<String, Object>result = null;
	
	@PostMapping(value="/claim/insert")
	public Map<String, Object>claimInsert(@RequestBody FullClaimDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		try {
			boolean success = service.claimInsert(dto);
			result.put("success", success);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@PostMapping(value="/claim/update")
	public Map<String, Object>claimUpdate(@RequestBody ClaimDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		try {
			boolean success = service.claimUpdate(dto);
			result.put("success", success);	
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
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
	
	@GetMapping(value="/claim/detail/{claim_idx}")
	public Map<String, Object>claimDetailByIdx(@PathVariable int claim_idx){
		log.info("idx = "+ claim_idx);
		result= new HashMap<String, Object>();
		ClaimDTO dto = service.claimDetailByIdx(claim_idx);
		result.put("dto", dto);
		return result;
	}
	
	@PostMapping(value="/returnProduct/update")
	public Map<String, Object>returnProductUpdate(@RequestBody ReturnProductDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.returnProductUpdate(dto);
		result.put("success", success);	
		return result;
	}
	
	@PostMapping(value="/returnProduct/list")
	public Map<String, Object>returnProductList(@RequestBody Map<String, Object> param){
		log.info("param : {}",param);
		return service.returnProductList(param);
	}
	
	@GetMapping(value="/returnProduct/del/{return_product_idx}")
	public Map<String, Object>returnProductDel(@PathVariable int return_product_idx){
		log.info("idx = "+ return_product_idx);
		result= new HashMap<String, Object>();
		boolean success = service.returnProductDel(return_product_idx);
		result.put("success", success);
		return result;
	}
	
	@GetMapping(value="/returnProduct/byClaimIdx/{claim_idx}")
	public Map<String, Object>returnProductByClaimIdx(@PathVariable int claim_idx){
		log.info("idx = "+ claim_idx);
		result = new HashMap<String, Object>();
		List<ReturnProductDTO>list = service.returnProductByClaimIdx(claim_idx);
		result.put("list", list);
		return result;
	}
}
