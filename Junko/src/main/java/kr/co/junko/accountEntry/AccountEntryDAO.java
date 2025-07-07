package kr.co.junko.accountEntry;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.AccountingEntryDTO;

@Mapper
public interface AccountEntryDAO {

	ArrayList<AccountingEntryDTO> accountList(int offset, int limit);

	int pages(int limit);

	int accountRegist(AccountingEntryDTO dto);

	Map<String, Object> acountDetail(int entry_idx);

	boolean accoutUpdate(int entry_idx, AccountingEntryDTO dto, String user_id);

	boolean accountDelete(int entry_idx, String user_id);

}
