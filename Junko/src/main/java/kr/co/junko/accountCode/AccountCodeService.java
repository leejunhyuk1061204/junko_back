package kr.co.junko.accountCode;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.AccountCodeDTO;

@Service
public class AccountCodeService {

    @Autowired AccountCodeDAO dao;

    Map<String, Object> result = null;

    public boolean accountInsert(AccountCodeDTO dto) {
        int row = dao.accountInsert(dto);
        return row > 0;
    }

    public boolean accountUpdate(AccountCodeDTO dto) {
        int row = dao.accountUpdate(dto);
        return row > 0;
    }

    public boolean accountDel(int as_idx) {
        int row = dao.accountDel(as_idx);
        return row > 0;
    }

    public List<AccountCodeDTO> accountList() {
        return dao.accountList();
    }

}
