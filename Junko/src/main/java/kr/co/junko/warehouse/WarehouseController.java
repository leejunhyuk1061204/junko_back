package kr.co.junko.warehouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.WarehouseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class WarehouseController {

	private final WarehouseService service;
	Map<String, Object>result = null;
	
	// 창고 등록
	@PostMapping(value="/warehouse/insert")
	public Map<String, Object> warehouseInsert(@RequestBody WarehouseDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.warehouseInsert(dto);
		result.put("success", success);
		return result;
	}
	
	// 창고 수정
	@PostMapping(value="/warehouse/update")
	public Map<String, Object> warehouseUpdate(@RequestBody WarehouseDTO dto){
		log.info("param : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.warehouseUpdate(dto);
		result.put("success", success);
		return result;
	}
	
	// 창고 리스트
	@PostMapping(value="/warehouse/list")
	public Map<String, Object> warehouseList(@RequestBody Map<String, Object>param){
		log.info("param : {}",param);
		return service.warehouseList(param);
	}
	
	// 창고 삭제
	@GetMapping(value="/warehouse/del/{warehouse_idx}")
	public Map<String, Object>warehouseDel(@PathVariable int warehouse_idx){
		log.info("idx : " + warehouse_idx);
		result = new HashMap<String, Object>();
		boolean success = service.warehouseDel(warehouse_idx);
		result.put("success", success);
		return result;
	}
}
