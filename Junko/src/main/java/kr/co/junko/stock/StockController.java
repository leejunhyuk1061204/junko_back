package kr.co.junko.stock;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.StockDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class StockController {

	private final StockService service;
	Map<String, Object>result = null;
	
	@GetMapping(value="/stock/detail/{stock_idx}")
	public Map<String, Object> stockDetailByIdx(@PathVariable int stock_idx){
		log.info("idx ="+stock_idx);
		result = new HashMap<String, Object>();
		StockDTO dto = service.stockDetailByIdx(stock_idx);
		result.put("dto", dto);
		return result;
	}
	
	// 기록 보기
	@PostMapping(value="/stock/list")
	public Map<String, Object>stockList(@RequestBody Map<String, Object>param){	
		log.info("param : {}",param);
		return service.stockList(param);
	}
	
	// 재고 보기 ("group" : ["option","manufacture","expiration","warehouse","zone"])
	@PostMapping(value="/stock/sum/list")
	public Map<String, Object>StockListByProduct(@RequestBody Map<String, Object> param){
		log.info("param : {}",param);
		return service.StockSumList(param);
	}
	
	@PostMapping(value="/stock/update")
	public Map<String, Object>stockUpdate(@RequestBody StockDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.stockUpdate(dto);
		result.put("success", success);
		return result;
	}

	@PostMapping(value="/stock/insert")
	public Map<String, Object>stockInsert(@RequestBody StockDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		try {
			boolean success = service.stockInsert(dto);
			result.put("success", success);
		} catch (Exception e) {
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@GetMapping(value="/stock/del/{stock_idx}")
	public Map<String, Object>stockDel(@PathVariable int stock_idx){
		result = new HashMap<String, Object>();
		boolean success = service.stockDel(stock_idx);
		result.put("success", success);
		return result;
	}
	
}
