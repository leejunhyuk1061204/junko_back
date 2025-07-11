package kr.co.junko.purchaseSettlement;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
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

import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.PurchaseSettlementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
public class PurchasesettlementController {
	
	@Autowired
	private final PurchasesettlementService service;
	Map<String, Object> result = null;
	

	//리스트 
	@GetMapping("/{settlement_idx:[0-9]+}")
	public Map<String, Object> getSettlement(@PathVariable int settlement_idx) {
		result = new HashMap<String, Object>();
		PurchaseSettlementDTO dto = service.getSettlementById(settlement_idx);

		if (dto != null) {
			result.put("result", "success");
			result.put("data", dto);
		} else {
			result.put("result", "fail");
			result.put("message", "해당 정산이 존재하지 않습니다.");
		}
		return result;
	}
	
	// 정산 등록 
	@PostMapping(value="/psRegister")
	public Map<String, Object> psRegister(@RequestBody PurchaseSettlementDTO dto) {
		result = new HashMap<String, Object>();

		service.psRegister(dto);
		result.put("result", "success");
		result.put("data", dto); 

		return result;
	}
	
	// 정산 업데이트 
	@PutMapping(value="/settlementUpdate/{settlement_idx}")
	public Map<String, Object> settlementUpdate(@PathVariable("settlement_id") int settlement_idx,
	                                            @RequestBody PurchaseSettlementDTO dto) {
		result = new HashMap<String, Object>();

		dto.setSettlement_idx(settlement_idx);
		int updated = service.settlementUpdate(dto); // 수정 성공 시 1 반환

		if (updated > 0) {
			result.put("result", "success");
			result.put("message", "정산 수정 완료");
			result.put("settlement_id", settlement_idx);
		} else {
			result.put("result", "fail");
			result.put("message", "수정할 수 없는 상태입니다. (예: 이미 확정됨)");
		}
		return result;
	}
	
	
	// 정산 삭제 
	@DeleteMapping(value="/settlementDel/{settlement_idx}")
	public Map<String, Object> settlementDel(@PathVariable("settlement_idx") int settlement_idx) {
		result = new HashMap<String, Object>();

		int deleted = service.settlementDel(settlement_idx); // 삭제 성공 시 1 반환

		if (deleted > 0) {
			result.put("result", "success");
			result.put("message", "정산 삭제 완료");
			result.put("settlement_id", settlement_idx);
		} else {
			result.put("result", "fail");
			result.put("message", "삭제할 수 없는 상태입니다. (예: 이미 확정됨)");
		}
		return result;
	}
	
	
	// 정산 확정
	@PostMapping("/settlementFinal/{settlement_idx}")
	public Map<String, Object> settlementFinal(@PathVariable int settlement_idx) {
	    result = new HashMap<String, Object>();

	    int row = service.settlementFinal(settlement_idx);

	    if (row > 0) {
	        result.put("result", "success");
	        result.put("message", "정산이 확정되었습니다.");
	    } else {
	        result.put("result", "fail");
	        result.put("message", "확정할 수 없는 상태입니다. (이미 확정 또는 마감)");
	    }
	    return result;
	}

	
	// 재정산 요청 
	@PostMapping(value="/settlementReq/{settlement_idx}")
	public Map<String, Object> settlementReq(@PathVariable int settlement_idx) {
	    Map<String, Object> result = new HashMap<String, Object>();

	    boolean req = service.settlementReq(settlement_idx);

	    if (req) {
	        result.put("result", "success");
	        result.put("message", "재정산 요청이 완료되었습니다.");
	    } else {
	        result.put("result", "fail");
	        result.put("message", "재정산 요청이 불가능한 상태입니다.");
	    }
	    return result;
	}

	
	// 관리자 재정산 승인 
	@PostMapping(value="/settlementAdminReq/{settlement_idx}")
	public Map<String, Object> approveReopen(@PathVariable int settlement_idx) {
	    Map<String, Object> result = new HashMap<String, Object>();

	    boolean approved = service.settlementAdminReq(settlement_idx);

	    if (approved) {
	        result.put("result", "success");
	        result.put("message", "재정산이 승인되어 진행중 상태로 전환되었습니다.");
	    } else {
	        result.put("result", "fail");
	        result.put("message", "재정산 승인할 수 없는 상태입니다.");
	    }

	    return result;
	}

	
	//정산서 pdf 자동 생성
	
	
	
	// 증빙파일 첨부 
	@PostMapping(value="/settlementFileUpload/{settlement_idx}/attachments")
	public Map<String, Object> settlementFileUpload(@PathVariable int settlement_idx,
	                                             @RequestParam("files") MultipartFile[] files,
	                                             @RequestParam(value = "type", required = false) String type) {
	    Map<String, Object> result = new HashMap<String, Object>();

	    try {
	        List<FileDTO> uploaded = service.settlementFileUpload(settlement_idx, files, type);
	        result.put("result", "success");
	        result.put("files", uploaded);
	    } catch (Exception e) {
	        result.put("result", "fail");
	        result.put("message", "파일 업로드 실패: " + e.getMessage());
	    }

	    return result;
	}

	
	// 증빙 첨부파일 조회
	@GetMapping(value="/settlementFileList/{settlement_idx}")
	public Map<String, Object> settlementFileList(@PathVariable int settlement_idx) {
	    Map<String, Object> result = new HashMap<String, Object>();

	    List<FileDTO> files = service.settlementFileList(settlement_idx);
	    result.put("result", "success");
	    result.put("files", files);

	    return result;
	}

	// 증빙 첨부파일 삭제
	@DeleteMapping(value="/settlementFileDel/{idx}")
	public Map<String, Object> settlementFileDel(@PathVariable int idx) {
	    Map<String, Object> result = new HashMap<>();

	    int deleted = service.settlementFileDel(idx);

	    if (deleted > 0) {
	        result.put("result", "success");
	        result.put("message", "첨부파일이 삭제되었습니다.");
	    } else {
	        result.put("result", "fail");
	        result.put("message", "파일을 삭제할 수 없습니다.");
	    }

	    return result;
	}

	// 증빙 파일 다운로드 
	@GetMapping(value="/settlementFileDown/{idx}")
	public ResponseEntity<FileSystemResource> settlementFileDown(@PathVariable int idx) {
	    FileDTO file = service.settlementFileDown(idx);
	    
	    if (file == null || file.isDel_yn()) {
	        return ResponseEntity.notFound().build();
	    }

	    String uploadDir = "/upload/settlements/"; // 저장 경로 (서버 내 실제 위치)
	    File filePath = new File(uploadDir + file.getNew_filename());

	    if (!filePath.exists()) {
	        return ResponseEntity.notFound().build();
	    }

	    FileSystemResource resource = new FileSystemResource(filePath);

	    return ResponseEntity.ok()
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOri_filename() + "\"")
	            .body(resource);
	}

	// pdf 자동생성 
	@PostMapping("/settlementPdf")
	public Map<String, Object> settlementPdf(@RequestParam int settlement_idx, @RequestParam int template_idx) {
	    result = new HashMap<String, Object>();
	    try {
	        FileDTO file = service.settlementPdf(settlement_idx, template_idx);
	        result.put("success", true);
	        result.put("file_path", "C:/upload/pdf/" + file.getNew_filename());
	        result.put("file_idx", file.getFile_idx());
	        result.put("filename", file.getOri_filename());
	    } catch (Exception e) {
	        log.error("정산 PDF 생성 실패", e);
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
