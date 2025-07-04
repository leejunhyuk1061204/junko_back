package kr.co.junko.warehouse;

import java.util.HashMap;
import java.util.Map;

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
	
}
