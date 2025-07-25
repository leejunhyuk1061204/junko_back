package kr.co.junko.purchaseSettlement;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.PurchaseSettlementDTO;

@Mapper
public interface PurchasesettlementDAO {

	PurchaseSettlementDTO getSettlementById(int settlement_idx);

	int psRegister(PurchaseSettlementDTO dto);


	int settlementUpdate(PurchaseSettlementDTO dto);

	int settlementDel(int settlement_idx);

	int settlementFinal(int settlement_idx);

	int settlementReq(int settlement_idx);

	int settlementAdminReq(int settlement_idx);

	int settlementFileUpload(FileDTO dto);

	List<FileDTO> settlementFileList(int settlement_idx);

	int settlementFileDel(int idx);

	FileDTO settlementFileDown(int idx);

	List<PurchaseSettlementDTO> getFilteredSettlements(
			  @Param("status") String status,
			  @Param("customName") String customName,
			  @Param("start") String start,
			  @Param("end") String end
			);

	int userIdxByLoginId(String loginId);

}
