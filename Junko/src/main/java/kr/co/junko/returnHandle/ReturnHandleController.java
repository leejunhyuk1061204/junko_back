package kr.co.junko.returnHandle;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.ReturnHandleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class ReturnHandleController {

	private final ReturnHandleService service;
	Map<String, Object>result = null;
	
	@PostMapping(value="/returnHandle/update")
	public Map<String, Object>returnHandleUpdate(@RequestBody ReturnHandleDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.returnHandleUpdate(dto);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/returnHandle/list")
	public Map<String, Object>returnHandleList(@RequestBody Map<String, Object> param){
		log.info("param : {}",param);
		return service.returnHandleList(param);
	}
	
	@GetMapping(value="/returnHandle/del/{return_handle_idx}")
	public Map<String, Object>returnHandleDel(@PathVariable int return_handle_idx){
		result = new HashMap<String, Object>();
		boolean success = service.returnHandleDel(return_handle_idx);
		result.put("success", success);
		return result;
	}
}
