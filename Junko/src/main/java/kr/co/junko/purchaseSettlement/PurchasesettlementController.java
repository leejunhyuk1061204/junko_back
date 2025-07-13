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
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin
public class PurchasesettlementController {
	
	@Autowired
	private final PurchasesettlementService service;

	@GetMapping("/{settlement_idx:[0-9]+}")
	public Map<String, Object> getSettlement(@PathVariable int settlement_idx) {
		Map<String, Object> result = new HashMap<>();
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

	
	@GetMapping("/settlementList")
	public Map<String, Object> getSettlementList(
	    @RequestParam(required = false) String status,
	    @RequestParam(required = false) String customName,
	    @RequestParam(required = false) String startDate,
	    @RequestParam(required = false) String endDate
	) {
	    Map<String, Object> result = new HashMap<String, Object>();
	    List<PurchaseSettlementDTO> list = service.getFilteredSettlements(status, customName, startDate, endDate);
	    result.put("result", "success");
	    result.put("data", list);
	    return result;
	}

	
	
	@PostMapping(value="/psRegister")
	public Map<String, Object> psRegister(@RequestBody PurchaseSettlementDTO dto) {
		Map<String, Object> result = new HashMap<>();
		int inserted = service.psRegister(dto);

		if (inserted > 0) {
			result.put("result", "success");
			result.put("data", dto); 
		} else {
			result.put("result", "fail");
			result.put("message", "정산 등록 실패");
		}
		return result;
	}

	@PutMapping(value="/settlementUpdate/{settlement_idx}")
	public Map<String, Object> settlementUpdate(@PathVariable int settlement_idx,
												@RequestBody PurchaseSettlementDTO dto) {
		Map<String, Object> result = new HashMap<>();
		dto.setSettlement_idx(settlement_idx);
		int updated = service.settlementUpdate(dto);

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

	@DeleteMapping(value="/settlementDel/{settlement_idx}")
	public Map<String, Object> settlementDel(@PathVariable int settlement_idx) {
		Map<String, Object> result = new HashMap<>();
		int deleted = service.settlementDel(settlement_idx);

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

	@PostMapping("/settlementFinal/{settlement_idx}")
	public Map<String, Object> settlementFinal(@PathVariable int settlement_idx) {
	    Map<String, Object> result = new HashMap<>();
	    log.info("정산 확정 요청 들어옴: {}", settlement_idx);

	    try {
	        int row = service.settlementFinal(settlement_idx);
	        log.info("service 결과: {}", row);

	        if (row > 0) {
	            result.put("result", "success");
	            result.put("message", "정산이 확정되었습니다.");
	        } else {
	            result.put("result", "fail");
	            result.put("message", "확정할 수 없는 상태입니다.");
	        }
	    } catch (Exception e) {
	        log.error("정산 확정 중 에러 발생", e);
	        result.put("result", "error");
	        result.put("message", e.getMessage());
	    }

	    log.info("응답 내용: {}", result);
	    return result;
	}


	@PostMapping(value="/settlementReq/{settlement_idx}")
	public Map<String, Object> settlementReq(@PathVariable int settlement_idx) {
		Map<String, Object> result = new HashMap<>();
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

	@PostMapping(value="/settlementAdminReq/{settlement_idx}")
	public Map<String, Object> approveReopen(@PathVariable int settlement_idx) {
		Map<String, Object> result = new HashMap<>();
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

	@PostMapping(value="/settlementFileUpload/{settlement_idx}/attachments")
	public Map<String, Object> settlementFileUpload(@PathVariable int settlement_idx,
													@RequestParam("files") MultipartFile[] files,
													@RequestParam(value = "type", required = false) String type) {
		Map<String, Object> result = new HashMap<>();
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

	@GetMapping(value="/settlementFileList/{settlement_idx}")
	public Map<String, Object> settlementFileList(@PathVariable int settlement_idx) {
		Map<String, Object> result = new HashMap<>();
		List<FileDTO> files = service.settlementFileList(settlement_idx);
		result.put("result", "success");
		result.put("files", files);
		return result;
	}

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

	@GetMapping(value="/settlementFileDown/{idx}")
	public ResponseEntity<FileSystemResource> settlementFileDown(@PathVariable int idx) {
		FileDTO file = service.settlementFileDown(idx);

		if (file == null || file.isDel_yn()) {
			return ResponseEntity.notFound().build();
		}

		String uploadDir = "/upload/settlements/";
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

	@PostMapping("/settlementPdf")
	public Map<String, Object> settlementPdf(@RequestParam int settlement_idx, @RequestParam int template_idx) {
		Map<String, Object> result = new HashMap<>();
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