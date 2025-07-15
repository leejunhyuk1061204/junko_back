package kr.co.junko.accountEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.junko.dto.AccountingEntryDTO;
import kr.co.junko.dto.AccountingEntryLogDTO;
import kr.co.junko.dto.AccountingEntrySearchDTO;
import kr.co.junko.dto.FileDTO;

@Mapper
public interface AccountEntryDAO {

	ArrayList<AccountingEntryDTO> accountList(int offset, int limit);

	int pages(int limit);

	int accountRegist(AccountingEntryDTO dto);

	Map<String, Object> accountDetail(int entry_idx);

	boolean accountUpdate(
			@Param("entry_idx") int entry_idx, 
			@Param("dto") AccountingEntryDTO dto, 
			@Param("user_id") String user_id);

	boolean accountDelete(int entry_idx, String user_id);

	// 현재 상태 조회 
	Map<String, Object> getEntryWriterAndStatus(int entry_idx);

	// 상태변경 
	void accountStatusUpdate(@Param("entry_idx") int entry_idx,
            @Param("status") String status);


	// 로그 기록 
	void saveLog(AccountingEntryLogDTO dto);

	void accountFile(FileDTO dto);

	List<FileDTO> entryFileList(int entry_idx);

	FileDTO entryFileDown(int file_idx);

	List<AccountingEntryLogDTO> accountLog(int entry_idx);

	void accountPdf(FileDTO file);

	List<AccountingEntryDTO> accountListSearch(AccountingEntrySearchDTO dto);

	int accountListSearchCount(AccountingEntrySearchDTO dto);

	int userIdxByLoginId(String loginId);
 
	int accountTotalCount();

	int findCustomIdxByName(String name);

	int findSalesIdxByName(String name);

	void deletePdfByEntryIdx(int entry_idx);

	FileDTO getPdfFileByEntryIdx(int entry_idx);

	void deletePdfPhysicallyByEntryIdx(int entry_idx);

}
