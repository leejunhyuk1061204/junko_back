package kr.co.junko.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.CustomDTO;
import kr.co.junko.util.Jwt;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class CustomController {

	@Autowired CustomService service;
	
	Map<String, Object> result = null;
	
	// 거래처 등록
	@PostMapping(value="/custom/insert")
	public Map<String, Object>customInsert(
			@RequestBody CustomDTO dto,
			@RequestHeader Map<String, String> header){
		
		log.info("dto : {}", dto);
		result = new HashMap<String, Object>();
		
		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		String loginId = (String) payload.get("user_id");

		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;
		
		if (login) {
			success = service.customInsert(dto);
		}
		
		result.put("success", success);
		result.put("loginYN", loginId);
		
		return result;
	}
	
	// 거래처 수정
	@PutMapping(value="/custom/update")
	public Map<String, Object>customUpdate(
			@RequestBody CustomDTO dto,
			@RequestHeader Map<String, String> header){
		
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		
		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		String loginId = (String) payload.get("user_id");

		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;
		
		if (login) {
			success = service.customUpdate(dto);
		}
		
		result.put("success", success);
		result.put("loginYN", loginId);
		
		return result;
	}
	
	// 거래처 삭제
	@PutMapping(value="/custom/del")
	public Map<String, Object>customDel(
			@RequestParam int custom_idx,
			@RequestHeader Map<String, String> header){
		
		log.info("custom_idx : {}",custom_idx);
		result = new HashMap<String, Object>();
		
		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		String loginId = (String) payload.get("user_id");

		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;
		
		if (login) {
			success = service.customDel(custom_idx);
		}
		
		result.put("success", success);
		result.put("loginYN", login);
		
		return result;
	}
	
	// 거래처 리스트
    @GetMapping("/custom/list")
    public List<CustomDTO> customList(@RequestParam Map<String, Object> param) {
        return service.customList(param);
    }
    
    // 거래처 총 갯수
    @GetMapping("/custom/cnt")
    public int customCnt(@RequestParam Map<String, Object> param) {
    	return service.customCnt(param);
    }
    
    // 거래처 상세 검색
    @GetMapping("/custom/select")
    public Map<String, Object> customSelect(@RequestParam int custom_idx) {
        Map<String, Object> result = new HashMap<>();
        CustomDTO dto = service.customSelect(custom_idx);

        if (dto == null) {
            result.put("success", false);
            result.put("message", "해당 거래처가 존재하지 않거나 삭제됨");
        } else {
            result.put("success", true);
            result.put("data", dto);
        }

        return result;
    }

	
}
