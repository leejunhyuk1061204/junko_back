package kr.co.junko.sales;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.FullSalesDTO;
import kr.co.junko.dto.SalesDTO;
import kr.co.junko.dto.SalesProductDTO;
import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class SalesController {

	private final SalesService service;
	Map<String, Object>result = null;
	
	@PostMapping(value="/sales/insert")
	public Map<String, Object>salesInsert(@RequestBody FullSalesDTO dto,@RequestHeader Map<String, String>header){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		String token = header.get("authorization");
		Map<String, Object>payload = Jwt.readToken(token);
		String loginId = (String)payload.get("user_id");
		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;
		if(login) {
			try {
				success =service.salesInsert(dto);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("msg", e.getMessage());
			}
		}
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/sales/update")
	public Map<String, Object>salesUpdate(@RequestBody SalesDTO dto,@RequestHeader Map<String, String> header){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		String token = header.get("authorization");
		Map<String, Object>payload = Jwt.readToken(token);
		String loginId = (String)payload.get("user_id");
		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;
		if(login) {
			success =service.salesUpdate(dto);
		}
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
	public Map<String, Object> salesCsvInsert(@RequestParam("file") MultipartFile file,@RequestHeader Map<String, String>header){
		log.info("CSV file : {}",file);
		result = new HashMap<String, Object>();
		String token = header.get("authorization");
		Map<String, Object>payload = Jwt.readToken(token);
		String loginId = (String)payload.get("user_id");
		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;
		if(login) {
			try {
				success = service.salesCsvInsert(file);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("msg", e.getMessage());
			}
		}
		result.put("success", success);
		return result;
	}
	
	@GetMapping(value="/sales/detail/{sales_idx}")
	public Map<String, Object>salesDetailByIdx(@PathVariable int sales_idx){
		log.info("idx = "+sales_idx);
		result = new HashMap<String, Object>();
		SalesDTO dto = service.salesDetailByIdx(sales_idx);
		result.put("dto", dto);
		return result;
	}
	
	@PostMapping(value="/salesProduct/update")
	public Map<String, Object>salesProductUpdate(@RequestBody SalesProductDTO dto){
		log.info("dot : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.salesProductUpdate(dto);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/salesProduct/list")
	public Map<String, Object>salesProductList(@RequestBody Map<String, Object>param){
		log.info("param : {}",param);
		return service.salesProductList(param);
	}
	
	@GetMapping(value="/salesProduct/del/{sales_product_idx}")
	public Map<String, Object> salesProductDel(@PathVariable int sales_product_idx){
		log.info("idx = "+ sales_product_idx);
		result = new HashMap<String, Object>();
		boolean success = service.salesProductDel(sales_product_idx);
		result.put("success", success);
		return result;
	}
	
}
