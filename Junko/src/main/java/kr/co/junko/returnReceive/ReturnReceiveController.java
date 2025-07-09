package kr.co.junko.returnReceive;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.ReturnReceiveDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReturnReceiveController {
	
	private final ReturnReceiveService service;
	Map<String, Object>result = null;
	
	@PostMapping(value="/returnReceive/update")
	public Map<String, Object>returnReceiveUpdate(@RequestBody ReturnReceiveDTO dto){
		log.info("dto : {}",dto);
		result = new HashedMap<String, Object>();
		boolean success = service.returnReceiveUpdate(dto);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/returnReceive/list")
	public Map<String, Object>returnReceiveList(@RequestBody Map<String, Object> param){
		log.info("param : {}",param);
		return service.returnReceiveList(param);
	}
	
	@GetMapping(value="/returnReceive/del/{return_receive_idx}")
	public Map<String, Object>returnReceiveDel(@PathVariable int return_receive_idx){
		log.info("dto : {}",return_receive_idx);
		result = new HashedMap<String, Object>();
		boolean success = service.returnReceiveDel(return_receive_idx);
		result.put("success", success);
		return result;
	}
	
}
