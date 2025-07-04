package kr.co.junko.account;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.AccountingEntryDTO;

@Mapper
public interface AccountEntryDAO {

	ArrayList<AccountingEntryDTO> accountList(int offset, int limit);

	int pages(int limit);

	int accountRegist(AccountingEntryDTO dto);

}
