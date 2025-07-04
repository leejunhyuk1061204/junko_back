package kr.co.junko.account;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.AccountingEntryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountEntryService {
	
	@Autowired
	private final AccountEntryDAO dao;
	private int limit = 10 , page = 0; //1페이지당 뜨는 리스트 10개
	
	public Map<String, Object> accountList(String page) {
		Map<String, Object> result = new HashMap<String, Object>();
		this.page=Integer.parseInt(page);
		result.put("page", this.page);
		int offset = (this.page-1) * limit;
		result.put("list", dao.accountList(offset,limit));
		result.put("pages", dao.pages(limit));
		
		return result;
	}

	public boolean accountRegist(AccountingEntryDTO dto) {
		int row = dao.accountRegist(dto);
		return row > 0 ? true : false;
	}

}
