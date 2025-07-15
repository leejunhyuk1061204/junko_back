package kr.co.junko.accountEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.AccountingEntryDTO;
import kr.co.junko.dto.AccountingEntrySearchDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
@CrossOrigin
public class AccountEntryController {

	@Autowired
	private final AccountEntryService service;
	private Map<String, Object> result = null;

	@GetMapping("/favicon.ico")
	public void favicon() {}

	// 전표 리스트 페이징
	@GetMapping("/accountList/{page}")
	public Map<String, Object> accountList(@PathVariable String page) {
		return service.accountList(page);
	}

	// 전표 등록 (토큰 기반 유저 인증 포함)
	@PostMapping("/accountRegist")
	public Map<String, Object> accountRegist(@RequestBody AccountingEntryDTO dto,
	                                         @RequestHeader Map<String, String> header) {
		result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			int user_idx = service.userIdxByLoginId(loginId);
			dto.setUser_idx(user_idx);
			boolean success = service.accountRegist(dto);
			result.put("success", success);
			result.put("loginYN", true);
		} else {
			result.put("success", false);
			result.put("loginYN", false);
		}
		return result;
	}

	// 전표 상세조회
	@GetMapping("/accountDetail/{entry_idx}")
	public Map<String, Object> accountDetail(@PathVariable int entry_idx) {
		return service.accountDetail(entry_idx);
	}

	// 검색, 필터, 정렬 리스트
	@PostMapping("/accountListSearch")
	public Map<String, Object> accountListSearch(@RequestBody AccountingEntrySearchDTO dto) {
		return service.accountListSearch(dto);
	}

	// 전표 수정
	@PutMapping("/accountUpdate/{entry_idx}")
	public Map<String, Object> accountUpdate(@PathVariable int entry_idx,
	                                         @RequestBody AccountingEntryDTO dto,
	                                         @RequestHeader Map<String, String> header) {
		result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			boolean success = service.accountUpdate(entry_idx, dto, loginId);
			result.put("success", success);
			result.put("loginYN", true);
		} else {
			result.put("success", false);
			result.put("loginYN", false);
		}
		return result;
	}

	// 전표 삭제
	@DeleteMapping("/accountDelete/{entry_idx}")
	public Map<String, Object> accountDelete(@PathVariable int entry_idx,
	                                         @RequestHeader Map<String, String> header) {
		result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			boolean success = service.accountDelete(entry_idx, loginId);
			result.put("success", success);
			result.put("loginYN", true);
		} else {
			result.put("success", false);
			result.put("loginYN", false);
		}
		return result;
	}

	// 전표 상태 변경
	@PatchMapping("/accountStatusUpdate/{entry_idx}/status")
	public ResponseEntity<?> accountStatusUpdate(@PathVariable int entry_idx,
	                                             @RequestBody Map<String, String> map,
	                                             @RequestHeader Map<String, String> header) {
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			int user_idx = service.userIdxByLoginId(loginId);
			String newStatus = map.get("status");
			String logMsg = map.getOrDefault("logMsg", null);

			service.accountStatusUpdate(entry_idx, newStatus, user_idx, logMsg);
			return ResponseEntity.ok().body(Map.of("success", true, "message", "상태 변경 완료!"));
		} else {
			return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인 필요"));
		}
	}

	// 전표 증빙파일 첨부 등록
	@PostMapping("/accountFile/{entry_idx}/upload")
	public Map<String, Object> accountFile(@PathVariable int entry_idx,
	                                       @RequestParam("file") MultipartFile file) {
		return service.accountFile(entry_idx, file);
	}

	// 전표 증빙파일 리스트
	@GetMapping("/entryFileList/{entry_idx}/upload")
	public Map<String, Object> entryFileList(@PathVariable int entry_idx) {
		return service.entryFileList(entry_idx);
	}

	// 전표 파일 다운로드
	@GetMapping("/entryFileDown/{file_idx}")
	public ResponseEntity<InputStreamResource> entryFileDown(@PathVariable int file_idx,
	                                                         @RequestParam(required = false) Boolean preview) throws IOException {
	    FileDTO dto = service.entryFileDown(file_idx);
	    String rootPath = "C:/upload";
	    if ("pdf".equalsIgnoreCase(dto.getType())) rootPath = "C:/upload/pdf";
	    File file = new File(rootPath, dto.getNew_filename());

	    if (!file.exists()) throw new FileNotFoundException("존재하지 않는 파일");

	    String encodedFilename = URLEncoder.encode(dto.getOri_filename(), "UTF-8").replaceAll("\\+", "%20");
	    HttpHeaders headers = new HttpHeaders();
	    headers.set(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file.toPath()));

	    if (Boolean.TRUE.equals(preview)) {
	        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encodedFilename); // 👈 미리보기
	    } else {
	        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename); // 👈 다운로드
	    }

	    InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
	    return ResponseEntity.ok().headers(headers).body(resource);
	}


	// 전표 이력 조회
	@GetMapping("/accountLog/{entry_idx}")
	public Map<String, Object> accountLog(@PathVariable int entry_idx) {
		return service.accountLog(entry_idx);
	}

	// 전표 PDF 생성
	@PostMapping("/accountPdf")
	public Map<String, Object> accountPdf(@RequestParam int entry_idx,
	                                      @RequestParam int template_idx) {
		result = new HashMap<>();
		try {
			FileDTO file = service.accountPdf(entry_idx, template_idx);
			result.put("file_path", "C:/upload/pdf/" + file.getNew_filename());
			result.put("file_idx", file.getFile_idx());
			result.put("filename", file.getOri_filename());
			result.put("success", true);
		} catch (Exception e) {
			log.error("전표 PDF 생성 오류", e);
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	}
}
