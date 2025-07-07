package kr.co.junko.purchaseSettlement;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.PurchaseSettlementDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class PurchasesettlementController {
	
	@Autowired
	private final PurchasesettlementService service =null;
	Map<String, Object> result = null;
	
	
	@GetMapping("/{settlement_id}")
	public Map<String, Object> getSettlement(@PathVariable int settlement_id) {
		result = new HashMap<String, Object>();
		PurchaseSettlementDTO dto = service.getSettlementById(settlement_id);

		if (dto != null) {
			result.put("result", "success");
			result.put("data", dto);
		} else {
			result.put("result", "fail");
			result.put("message", "해당 정산이 존재하지 않습니다.");
		}
		return result;
	}
	
	@PostMapping(value="/psRegister")
	public Map<String, Object> psRegister(@RequestBody PurchaseSettlementDTO dto) {
		result = new HashMap<String, Object>();

		service.psRegister(dto);
		result.put("result", "success");
		result.put("data", dto); 

		return result;
	}
	
	
	@PutMapping(value="/settlementUpdate/{settlement_id}")
	public Map<String, Object> settlementUpdate(@PathVariable("settlement_id") int settlement_id,
	                                            @RequestBody PurchaseSettlementDTO dto) {
		result = new HashMap<String, Object>();

		dto.setSettlement_idx(settlement_id);
		int updated = service.settlementUpdate(dto); // 수정 성공 시 1 반환

		if (updated > 0) {
			result.put("result", "success");
			result.put("message", "정산 수정 완료");
			result.put("settlement_id", settlement_id);
		} else {
			result.put("result", "fail");
			result.put("message", "수정할 수 없는 상태입니다. (예: 이미 확정됨)");
		}
		return result;
	}
	
	@DeleteMapping(value="/settlementDel/{settlement_id}")
	public Map<String, Object> settlementDel(@PathVariable("settlement_id") int settlement_id) {
		result = new HashMap<String, Object>();

		int deleted = service.settlementDel(settlement_id); // 삭제 성공 시 1 반환

		if (deleted > 0) {
			result.put("result", "success");
			result.put("message", "정산 삭제 완료");
			result.put("settlement_id", settlement_id);
		} else {
			result.put("result", "fail");
			result.put("message", "삭제할 수 없는 상태입니다. (예: 이미 확정됨)");
		}
		return result;
	}
	
	
	
	
	
	
	
	
}
