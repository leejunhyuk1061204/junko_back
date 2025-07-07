package kr.co.junko.purchaseSettlement;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.PurchaseSettlementDTO;

@Mapper
public interface PurchasesettlementDAO {

	PurchaseSettlementDTO getSettlementById(int settlement_id);

	Object psRegister(PurchaseSettlementDTO dto);

	int settlementUpdate(PurchaseSettlementDTO dto);

	int settlementDel(int settlement_id);

}
