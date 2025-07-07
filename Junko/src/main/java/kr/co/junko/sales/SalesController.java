package kr.co.junko.sales;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.SalesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SalesController {

	private final SalesService service;
	Map<String, Object>result = null;
	
	@PostMapping(value="/sales/insert")
	public Map<String, Object>salesInsert(@RequestBody SalesDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success =service.salesInsert(dto);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/sales/update")
	public Map<String, Object>salesUpdate(@RequestBody SalesDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success =service.salesUpdate(dto);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/sales/list")
	public Map<String, Object>salesList(@RequestBody Map<String, Object>param){
		log.info("param : {}",param);
		return service.salesList(param);
	}
	
	@GetMapping(value="/sales/del/{sales_idx}")
	public Map<String, Object>salesDel(@PathVariable int sales_idx){
		log.info("idx = "+sales_idx);
		result = new HashMap<String, Object>();
		boolean success = service.salesDel(sales_idx);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/sales/csv")
	public Map<String, Object> salesCsvInsert(@RequestParam("file") MultipartFile file){
		log.info("CSV file : {}",file);
		result = new HashMap<String, Object>();
		boolean success = service.salesCsvInsert(file);
		result.put("success", success);
		return result;
	}
}
