package kr.co.junko.collectionAndPayment;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.CollectionAndPaymentLogDTO;
import kr.co.junko.dto.CollectionAndPaymentRequestDTO;
import kr.co.junko.dto.CollectionAndPaymentResponseDTO;
import kr.co.junko.dto.CustomDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.LinkedItemDTO;

@Mapper
public interface CollectionAndPaymentDAO {

	boolean capRegist(String type, LocalDate date, int amount, int custom_idx);

	void insert(CollectionAndPaymentRequestDTO dto);

	CollectionAndPaymentResponseDTO capList(int cap_idx);

	void capUpdate(CollectionAndPaymentRequestDTO dto);

	int capDel(int cap_idx);

	List<CustomDTO> capCustom();

	List<CustomDTO> capCustomList();

	LinkedItemDTO getEntryList();

	LinkedItemDTO getSettlementList();

	LinkedItemDTO getInvoiceList();

	void insertFile(FileDTO dto);

	FileDTO getFileByIdx(int file_idx);

	void deleteFile(int file_idx);

	List<CollectionAndPaymentLogDTO> getLogsByCapIdx(int cap_idx);

	void insertLog(CollectionAndPaymentLogDTO dto);
	
	
}
