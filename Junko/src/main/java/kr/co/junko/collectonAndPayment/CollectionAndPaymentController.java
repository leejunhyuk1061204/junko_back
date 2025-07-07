package kr.co.junko.collectonAndPayment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.CollectionAndPaymentRequestDTO;
import kr.co.junko.dto.CollectionAndPaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CollectionAndPaymentController {

	@Autowired
	private final CollectionAndPaymentService service = null;
	Map<String, Object> result = null;
	
	// 등록 + 중복등록 방
	@PostMapping(value="/capRegist")
	public Map<String, Object> capRegist(@RequestBody CollectionAndPaymentRequestDTO dto) {
		result = new HashMap<String, Object>();
		
		try {
            service.capRegist(dto);  
            result.put("success", true);
            result.put("message", "입금/지급 등록 성공");
        } catch (Exception e) {
            log.error("입금/지급 등록 실패", e);
            result.put("success", false);
            result.put("message", "입금/지급 등록 중 오류 발생");
        }
		
		return result;
	}
	
	
	// 조회
	@GetMapping(value="/capList/{cap_idx}")
	public Map<String, Object> capList(@PathVariable int cap_idx) {
		result = new HashMap<String, Object>();
	    CollectionAndPaymentResponseDTO dto = service.capList(cap_idx);
	    
	    if (dto != null) {
	        result.put("success", true);
	        result.put("data", dto);
	    } else {
	        result.put("success", false);
	        result.put("message", "데이터 없음");
	    }

	    return result;
	}
	// 수정
	@PutMapping(value="/capUpdate/{cap_idx}")
	public Map<String, Object> capUpdate(@PathVariable int cap_idx,
	                                  @RequestBody CollectionAndPaymentRequestDTO dto) {
		result = new HashMap<String, Object>();
	    dto.setCap_idx(cap_idx);
	    boolean success = service.capUpdate(dto);

	    result.put("success", success);
	    result.put("message", success ? "수정 완료" : "수정 실패 또는 중복된 데이터");

	    return result;
	}

	// 삭제
	@DeleteMapping(value="/capDel/{cap_idx}")
	public Map<String, Object> capDel(@PathVariable int cap_idx) {
		result = new HashMap<String, Object>();
	    boolean success = service.capDel(cap_idx);

	    result.put("success", success);
	    result.put("message", success ? "삭제 완료" : "삭제 실패");
	    return result;
	}

	// 거래처 연동
	
	// 계좌 정보 표시
	
	// 전표 / 정산 / 세금계산서 연동
	
	// 증빙파일 첨부
	
	// pdf 자동 생성
	
	
	// 이력 관리 
	
	
	
}
