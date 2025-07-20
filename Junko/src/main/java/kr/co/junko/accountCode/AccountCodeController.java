package kr.co.junko.accountCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.AccountCodeDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class AccountCodeController {

    @Autowired AccountCodeService service;

    Map<String, Object> result = null;
 
    // 계정과목 등록
    @PostMapping("/account/insert")
    public Map<String, Object> accountInsert(@RequestBody AccountCodeDTO dto) {
        result = new HashMap<String, Object>();

        boolean success = service.accountInsert(dto);
        
        result.put("success", success);
        return result;
    }

    // 계정과목 수정
    @PutMapping("/account/update/{as_idx}")
    public Map<String, Object> accountUpdate(@PathVariable int as_idx, @RequestBody AccountCodeDTO dto) {
        dto.setAs_idx(as_idx);
        result = new HashMap<String, Object>();

        boolean success = service.accountUpdate(dto);

        result.put("success", success);
        return result;
    }

    // 계정과목 삭제 (논리 삭제)
    @PutMapping("/account/del/{as_idx}")
    public Map<String, Object> accountDel(@PathVariable int as_idx) {
        result = new HashMap<String, Object>();

        boolean success = service.accountDel(as_idx);

        result.put("success", success);
        return result;
    }

    // 계정과목 리스트
    @GetMapping("/account/list")
    public Map<String, Object> accountList() {
        result = new HashMap<String, Object>();

        List<AccountCodeDTO> list = service.accountList();

        result.put("success", true);
        result.put("list", list);
        return result;
    }


}