package kr.co.junko.collectionAndPayment;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.*;
import kr.co.junko.util.Jwt;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin
public class CollectionAndPaymentController {

	@Autowired
	private final CollectionAndPaymentService service = null;
	Map<String, Object> result = null;

	
	// 페이징처
	@PostMapping("/searchCapPaged")
	public Map<String, Object> searchCapPaged(@RequestBody CapSearchDTO dto) {
	    Map<String, Object> result = new HashMap<>();
	    result.put("success", true);
	    result.put("data", service.searchCapPaged(dto));
	    result.put("total", service.countSearchCap(dto)); // 전체 개수 반환
	    return result;
	}
	
	// 등록 (토큰 기반)
	@PostMapping("/capRegist")
	public Map<String, Object> capRegist(@RequestBody CollectionAndPaymentRequestDTO dto,
	                                     @RequestHeader Map<String, String> header) {
	    result = new HashMap<>();
	    String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

	    if (loginId != null && !loginId.isEmpty()) {
	        int user_idx = service.userIdxByLoginId(loginId);
	        dto.setUser_idx(user_idx);
	        try {
	            int cap_idx = service.capRegist(dto); 
	            result.put("success", true);
	            result.put("cap_idx", cap_idx);       
	            result.put("message", "입금/지급 등록 성공");
	        } catch (Exception e) {
	            log.error("입금/지급 등록 실패", e);
	            result.put("success", false);
	            result.put("message", "등록 중 오류 발생");
	        }
	    } else {
	        result.put("success", false);
	        result.put("message", "로그인 정보 없음");
	    }
	    return result;
	}

	// 조회
	@GetMapping("/capList/{cap_idx}")
	public Map<String, Object> capList(@PathVariable int cap_idx) {
		result = new HashMap<>();
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

	// 검색 정렬
	@PostMapping("/searchCap")
	public Map<String, Object> searchCap(@RequestBody CapSearchDTO dto) {
	    List<CollectionAndPaymentResponseDTO> list = service.searchCap(dto);

	    Map<String, Object> result = new HashMap<>();
	    result.put("success", true);
	    result.put("data", list);
	    return result;
	}

	// 수정 (토큰 기반)
	@PutMapping("/capUpdate/{cap_idx}")
	public Map<String, Object> capUpdate(@PathVariable int cap_idx,
	                                     @RequestBody CollectionAndPaymentRequestDTO dto,
	                                     @RequestHeader Map<String, String> header) {
		result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			int user_idx = service.userIdxByLoginId(loginId);
			dto.setUser_idx(user_idx);
			dto.setCap_idx(cap_idx);

			boolean success = service.capUpdate(dto);
			result.put("success", success);
			result.put("message", success ? "수정 완료" : "수정 실패 또는 중복된 데이터");
		} else {
			result.put("success", false);
			result.put("message", "로그인 정보 없음");
		}
		return result;
	}

	// 삭제 (토큰 기반)
	@DeleteMapping("/capDel/{cap_idx}")
	public Map<String, Object> capDel(@PathVariable int cap_idx,
	                                  @RequestHeader Map<String, String> header) {
		result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			boolean success = service.capDel(cap_idx);
			result.put("success", success);
			result.put("message", success ? "삭제 완료" : "삭제 실패");
		} else {
			result.put("success", false);
			result.put("message", "로그인 정보 없음");
		}
		return result;
	}

	// 거래처 연동
	@GetMapping("/capCustom")
	public Map<String, Object> capCustom() {
		result = new HashMap<>();
		List<CustomDTO> list = service.capCustom();
		result.put("success", true);
		result.put("data", list);
		return result;
	}

	// 계좌 정보
	@GetMapping("/capCustomList")
	public Map<String, Object> capCustomList() {
		result = new HashMap<>();
		List<CustomDTO> list = service.capCustomList();
		result.put("success", true);
		result.put("data", list);
		return result;
	}

	// 전표/정산/세금계산서 연동
	@GetMapping("/linked/all")
	public Map<String, Object> linkedItem() {
		List<LinkedItemDTO> resultList = new ArrayList<>();
		resultList.addAll(service.getEntryList());
		resultList.addAll(service.getSettlementList());
		resultList.addAll(service.getInvoiceList());

		Map<String, Object> result = new HashMap<>();
		result.put("success", true);
		result.put("data", resultList);
		return result;
	}

	// 파일 업로드
	@PostMapping("/capFile/{cap_idx}/upload")
	public Map<String, Object> capFile(@PathVariable int cap_idx,
	                                   @RequestParam("file") MultipartFile file) {
		return service.capFile("collection", cap_idx, file);
	}

	// 파일 다운로드
	@GetMapping("/capDown/{file_idx}")
	public ResponseEntity<FileSystemResource> capDown(@PathVariable int file_idx) {
		return service.capDown(file_idx);
	}

	// 파일 삭제
	@DeleteMapping("/capFileDel/{file_idx}")
	public Map<String, Object> capFileDel(@PathVariable int file_idx) {
		return service.capFileDel(file_idx);
	}

	// PDF 자동 생성
	@PostMapping("/capPdf")
	public Map<String, Object> capPdf(@RequestParam int cap_idx,
	                                  @RequestParam int template_idx) {
		result = new HashMap<>();
		try {
			FileDTO file = service.capPdf(cap_idx, template_idx);
			result.put("success", true);
			result.put("file_path", "C:/upload/pdf/" + file.getNew_filename());
			result.put("file_idx", file.getFile_idx());
			result.put("filename", file.getOri_filename());
		} catch (Exception e) {
			log.error("수금/지급 PDF 생성 오류", e);
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	}

	// 이력
	@GetMapping("/capLog/{cap_idx:[0-9]+}")
	public Map<String, Object> getLogs(@PathVariable int cap_idx) {
		result = new HashMap<>();
		List<CollectionAndPaymentLogDTO> logList = service.getLogsByCapIdx(cap_idx);
		result.put("success", true);
		result.put("data", logList);
		return result;
	}
}
