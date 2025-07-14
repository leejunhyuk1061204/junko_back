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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.PurchaseSettlementDTO;
import kr.co.junko.util.Jwt;
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
	public Map<String, Object> getSettlementList(@RequestParam(required = false) String status,
												 @RequestParam(required = false) String customName,
												 @RequestParam(required = false) String startDate,
												 @RequestParam(required = false) String endDate) {
		Map<String, Object> result = new HashMap<>();
		List<PurchaseSettlementDTO> list = service.getFilteredSettlements(status, customName, startDate, endDate);
		result.put("result", "success");
		result.put("data", list);
		return result;
	}

	// 정산 등록 (JWT)
	@PostMapping("/psRegister")
	public Map<String, Object> psRegister(@RequestBody PurchaseSettlementDTO dto,
	                                      @RequestHeader Map<String, String> header) {
		Map<String, Object> result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			int user_idx = service.userIdxByLoginId(loginId);
			dto.setUser_idx(user_idx);

			int inserted = service.psRegister(dto);
			if (inserted > 0) {
				result.put("result", "success");
				result.put("data", dto);
			} else {
				result.put("result", "fail");
				result.put("message", "정산 등록 실패");
			}
		} else {
			result.put("result", "fail");
			result.put("message", "로그인 정보 없음");
		}
		return result;
	}

	// 정산 수정 (JWT)
	@PutMapping("/settlementUpdate/{settlement_idx}")
	public Map<String, Object> settlementUpdate(@PathVariable int settlement_idx,
	                                            @RequestBody PurchaseSettlementDTO dto,
	                                            @RequestHeader Map<String, String> header) {
		Map<String, Object> result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			int user_idx = service.userIdxByLoginId(loginId);
			dto.setUser_idx(user_idx);
			dto.setSettlement_idx(settlement_idx);

			int updated = service.settlementUpdate(dto);
			if (updated > 0) {
				result.put("result", "success");
				result.put("message", "정산 수정 완료");
			} else {
				result.put("result", "fail");
				result.put("message", "수정할 수 없는 상태입니다.");
			}
		} else {
			result.put("result", "fail");
			result.put("message", "로그인 정보 없음");
		}
		return result;
	}

	// 정산 삭제 (JWT)
	@DeleteMapping("/settlementDel/{settlement_idx}")
	public Map<String, Object> settlementDel(@PathVariable int settlement_idx,
	                                         @RequestHeader Map<String, String> header) {
		Map<String, Object> result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			int deleted = service.settlementDel(settlement_idx);
			result.put("result", deleted > 0 ? "success" : "fail");
			result.put("message", deleted > 0 ? "정산 삭제 완료" : "삭제할 수 없는 상태입니다.");
		} else {
			result.put("result", "fail");
			result.put("message", "로그인 정보 없음");
		}
		return result;
	}

	// 정산 확정 (JWT)
	@PostMapping("/settlementFinal/{settlement_idx}")
	public Map<String, Object> settlementFinal(@PathVariable int settlement_idx,
	                                           @RequestHeader Map<String, String> header) {
		Map<String, Object> result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			try {
				int row = service.settlementFinal(settlement_idx);
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
		} else {
			result.put("result", "fail");
			result.put("message", "로그인 정보 없음");
		}
		return result;
	}

	// 재정산 요청 (JWT)
	@PostMapping("/settlementReq/{settlement_idx}")
	public Map<String, Object> settlementReq(@PathVariable int settlement_idx,
	                                         @RequestHeader Map<String, String> header) {
		Map<String, Object> result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			boolean req = service.settlementReq(settlement_idx);
			result.put("result", req ? "success" : "fail");
			result.put("message", req ? "재정산 요청 완료" : "요청 불가능한 상태");
		} else {
			result.put("result", "fail");
			result.put("message", "로그인 정보 없음");
		}
		return result;
	}

	// 관리자 재정산 승인 (JWT)
	@PostMapping("/settlementAdminReq/{settlement_idx}")
	public Map<String, Object> approveReopen(@PathVariable int settlement_idx,
	                                         @RequestHeader Map<String, String> header) {
		Map<String, Object> result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			boolean approved = service.settlementAdminReq(settlement_idx);
			result.put("result", approved ? "success" : "fail");
			result.put("message", approved ? "재정산 승인 완료" : "승인 불가능한 상태");
		} else {
			result.put("result", "fail");
			result.put("message", "로그인 정보 없음");
		}
		return result;
	}

	@PostMapping("/settlementFileUpload/{settlement_idx}/attachments")
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

	@GetMapping("/settlementFileList/{settlement_idx}")
	public Map<String, Object> settlementFileList(@PathVariable int settlement_idx) {
		Map<String, Object> result = new HashMap<>();
		List<FileDTO> files = service.settlementFileList(settlement_idx);
		result.put("result", "success");
		result.put("files", files);
		return result;
	}

	@DeleteMapping("/settlementFileDel/{idx}")
	public Map<String, Object> settlementFileDel(@PathVariable int idx) {
		Map<String, Object> result = new HashMap<>();
		int deleted = service.settlementFileDel(idx);
		result.put("result", deleted > 0 ? "success" : "fail");
		result.put("message", deleted > 0 ? "첨부파일 삭제됨" : "삭제 실패");
		return result;
	}

	@GetMapping("/settlementFileDown/{idx}")
	public ResponseEntity<FileSystemResource> settlementFileDown(@PathVariable int idx) {
		FileDTO file = service.settlementFileDown(idx);
		if (file == null || file.isDel_yn()) return ResponseEntity.notFound().build();

		File filePath = new File("/upload/settlements/" + file.getNew_filename());
		if (!filePath.exists()) return ResponseEntity.notFound().build();

		FileSystemResource resource = new FileSystemResource(filePath);
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOri_filename() + "\"")
				.body(resource);
	}

	@PostMapping("/settlementPdf")
	public Map<String, Object> settlementPdf(@RequestParam int settlement_idx,
	                                         @RequestParam int template_idx) {
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
