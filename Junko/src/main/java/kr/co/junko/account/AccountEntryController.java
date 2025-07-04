package kr.co.junko.account;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.AccountingEntryDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AccountEntryController {
	
	@Autowired
	private final AccountEntryService service = null;
	Map<String, Object> result = null;
	
	// 전표 리스트 페이징
	@GetMapping(value="accountList/{page}")
	public Map<String, Object> accountList(@PathVariable String page){

		result = new HashMap<String, Object>();
		result = service.accountList(page);
		return result;
	}
	
	// 전표 등록
	@PostMapping(value="/accountRegist")
	public Map<String, Object> accountRegist(@RequestBody AccountingEntryDTO dto){
		result = new HashMap<String, Object>();
		boolean success = service.accountRegist(dto);
		result.put("success", success);
		
		
		return result;
	}
	
}
