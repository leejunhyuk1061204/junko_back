package kr.co.junko.accountCode;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.AccountCodeDTO;

@Mapper
public interface AccountCodeDAO {

    int accountInsert(AccountCodeDTO dto);

    int accountUpdate(AccountCodeDTO dto);

    int accountDel(int as_idx);

    List<AccountCodeDTO> accountList();

}
