package kr.co.junko.receive;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.ReceiveDTO;
import kr.co.junko.dto.ReceiveProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReceiveController {

	private final ReceiveService service;
	Map<String, Object>result = null;
	
	// 입고 수정
	@PostMapping(value="/receive/update")
	public Map<String, Object>receiveUpdate(@RequestBody ReceiveDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		try {
			boolean success = service.receiveUpdate(dto);
			result.put("success", success);
		} catch (RuntimeException e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		
		return result;
	}
	
	// 입고 상품 수정
	@PostMapping(value="/receiveProduct/update")
	public Map<String, Object>receiveProductUpdate(@RequestBody ReceiveProductDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.receiveProductUpdate(dto);
		result.put("success", success);
		return result;
	}
	
	// 입고 리스트
	@PostMapping(value="/receive/list")
	public Map<String, Object>receiveList(@RequestBody Map<String, Object>param){
		log.info("param : {}",param);
		return service.receiveList(param);
	}
	
	// 입고 상품 리스트
	@PostMapping(value="/receiveProduct/list")
	public Map<String, Object>receiveProductList(@RequestBody Map<String, Object>param){
		log.info("param : {}",param);
		return service.receiveProductList(param);
	}
	
	// 입고 삭제(receive_idx, 트랜잭션)
	@GetMapping(value="/receive/del/{receive_idx}")
	public Map<String, Object>receiveDel(@PathVariable int receive_idx){
		log.info("idx = "+receive_idx);
		result = new HashMap<String, Object>();
		
		try {
			boolean success = service.receiveDel(receive_idx);
			result.put("success", success);
		} catch (RuntimeException e) {
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		
		return result;
	}
}
