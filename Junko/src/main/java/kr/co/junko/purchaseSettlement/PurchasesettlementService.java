package kr.co.junko.purchaseSettlement;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.PurchaseSettlementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchasesettlementService {

	
	@Autowired
	private final PurchasesettlementDAO dao;
	Map<String, Object> result = null;
	
	public PurchaseSettlementDTO getSettlementById(int settlement_id) {
		return dao.getSettlementById(settlement_id);
	}
	public Object psRegister(PurchaseSettlementDTO dto) {
		return dao.psRegister(dto);
		
	}
	public int settlementUpdate(PurchaseSettlementDTO dto) {
		return dao.settlementUpdate(dto);
	}
	public int settlementDel(int settlement_id) {
		return dao.settlementDel(settlement_id);
	}
	
}
