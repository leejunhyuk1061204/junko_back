package kr.co.junko.collectonAndPayment;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.CollectionAndPaymentLogDTO;
import kr.co.junko.dto.CollectionAndPaymentRequestDTO;
import kr.co.junko.dto.CollectionAndPaymentResponseDTO;
import kr.co.junko.dto.CustomDTO;
import kr.co.junko.dto.LinkedItemDTO;
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
	@GetMapping(value="/capCustom")
	public Map<String, Object> capCustom() {
		result = new HashMap<String, Object>();
	    List<CustomDTO> list = service.capCustom();

	    result.put("success", true);
	    result.put("data", list);
	    return result;
	}


	// 계좌 정보 표시
	@GetMapping(value="/capCustomList")
	public Map<String, Object> capCustomList() {
		result = new HashMap<String, Object>();
	    List<CustomDTO> list = service.capCustomList();

	    result.put("success", true);
	    result.put("data", list);
	    return result;
	}

	
	// 전표 / 정산 / 세금계산서 연동
	@GetMapping(value="/linked/all")
	public Map<String, Object> linkedItem() {
	    List<LinkedItemDTO> resultList = new ArrayList();
	    resultList.add(service.getEntryList());
	    resultList.add(service.getSettlementList());
	    resultList.add(service.getInvoiceList());

	    Map<String, Object> result = new HashMap<>();
	    result.put("success", true);
	    result.put("data", resultList);
	    return result;
	}
	
	
	// 증빙파일 첨부
	// 파일 업로드
    @PostMapping(value="/capFile/{cap_idx}/upload")
    public Map<String, Object> capFile(@PathVariable int cap_idx,
                                      @RequestParam("file") MultipartFile file) {
        return service.capFile("collection", cap_idx, file);
    }

    // 파일 다운로드
    @GetMapping(value="/capDown/{file_idx}")
    public ResponseEntity<FileSystemResource> capDown(@PathVariable int file_idx) {
        return service.capDown(file_idx);
    }

    // 파일 삭제
    @DeleteMapping(value="/capFileDel/{file_idx}")
    public Map<String, Object> capFileDel(@PathVariable int file_idx) {
        return service.capFileDel(file_idx);
    }
	
	
	// pdf 자동 생성
    
	
	// 이력 관리 
    @GetMapping("/{cap_idx}")
    public Map<String, Object> getLogs(@PathVariable int cap_idx) {
       result = new HashMap<String, Object>();
        List<CollectionAndPaymentLogDTO> logList = service.getLogsByCapIdx(cap_idx);

        result.put("success", true);
        result.put("data", logList);
        return result;
    }
	
	
}
