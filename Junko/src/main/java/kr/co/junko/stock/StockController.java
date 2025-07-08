package kr.co.junko.stock;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.StockDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
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
	
}
