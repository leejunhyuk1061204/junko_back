package kr.co.junko.accountDepartment;

import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.AccountingDepartmentDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.util.Jwt;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin
public class AccountDepartmentController {

	@Autowired
	private final AccountDepartmentService service = null;
	Map<String, Object> result = null;

	// 분개 리스트 조회
	@GetMapping("/accountDeptList/{entry_idx}/detail")
	public Map<String, Object> accountDeptList(@PathVariable int entry_idx) {
		result = new HashMap<>();
		List<AccountingDepartmentDTO> list = service.accountDeptList(entry_idx);
		result.put("result", "success");
		result.put("data", list);
		result.put("count", list.size());
		return result;
	}

	// 개별 분개 상세 조회
	@GetMapping("/accountDeptDetail/{entry_idx}/details/{dept_idx}")
	public Map<String, Object> accountDeptDetail(@PathVariable int entry_idx,
	                                             @PathVariable int dept_idx) {
		result = new HashMap<>();
		AccountingDepartmentDTO detail = service.accountDeptDetail(entry_idx, dept_idx);
		if (detail == null) {
			result.put("result", "fail");
			result.put("message", "해당 분개를 찾을 수 없습니다.");
		} else {
			result.put("result", "success");
			result.put("data", detail);
		}
		return result;
	}

	// 분개 등록 (JWT 인증)
	@PostMapping("/accountDeptAdd/{entry_idx}/details")
	public Map<String, Object> insertDept(@PathVariable int entry_idx,
	                                      @RequestBody AccountingDepartmentDTO dto,
	                                      @RequestHeader Map<String, String> header) {
		result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			int user_idx = service.userIdxByLoginId(loginId);
			dto.setUser_idx(user_idx);
			boolean success = service.accountDeptAdd(entry_idx, dto);
			result.put("result", success ? "success" : "fail");
			result.put("message", success ? "분개 등록 완료" : "분개 등록 실패");
			result.put("loginYN", true);
		} else {
			result.put("result", "fail");
			result.put("message", "로그인 정보 없음");
			result.put("loginYN", false);
		}
		return result;
	}

	// 분개 수정 (JWT 인증)
	@PutMapping("/accountDeptUpdate/{entry_idx}/details/{dept_idx}")
	public Map<String, Object> accountDeptUpdate(@PathVariable int entry_idx,
	                                             @PathVariable int dept_idx,
	                                             @RequestBody AccountingDepartmentDTO dto,
	                                             @RequestHeader Map<String, String> header) {
		result = new HashMap<>();
		String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			int user_idx = service.userIdxByLoginId(loginId);
			dto.setDept_idx(dept_idx);
			dto.setUser_idx(user_idx);
			boolean success = service.accountDeptUpdate(dto);
			result.put("result", success ? "success" : "fail");
			result.put("loginYN", true);
		} else {
			result.put("result", "fail");
			result.put("message", "로그인 정보 없음");
			result.put("loginYN", false);
		}
		return result;
	}

	// 분개 삭제
	@DeleteMapping("/accountDeptDelete/{entry_idx}/details/{dept_idx}")
	public Map<String, Object> accountDeptDelete(@PathVariable int entry_idx,
	                                             @PathVariable int dept_idx) {
		boolean success = service.accountDeptDelete(dept_idx);
		Map<String, Object> result = new HashMap<>();
		result.put("result", success ? "success" : "fail");
		return result;
	}

	// 분개 증빙파일 업로드
	@PostMapping("/accountDeptFile/{entry_idx}/details/{dept_idx}")
	public ResponseEntity<Map<String, Object>> accountDeptFile(@PathVariable int entry_idx,
	                                                            @PathVariable int dept_idx,
	                                                            @RequestParam("file") MultipartFile file) {
		Map<String, Object> result = new HashMap<>();
		try {
			service.accountDeptFile(file, "ENTRY_DETAIL", dept_idx);
			result.put("result", "success");
		} catch (Exception e) {
			result.put("result", "fail");
			result.put("message", e.getMessage());
		}
		return ResponseEntity.ok(result);
	}

	// 분개 증빙파일 다운로드
	@GetMapping("/deptfileDown/{file_idx}")
	public ResponseEntity<UrlResource> deptfileDown(@PathVariable int file_idx) {
		FileDTO dto = service.deptfileDown(file_idx);

		if (dto == null || dto.isDel_yn()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		try {
			String basePath = "C:/upload";
			Path filePath = Paths.get(basePath + dto.getNew_filename());
			UrlResource resource = new UrlResource(filePath.toUri());

			if (!resource.exists()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			String encodedName = URLEncoder.encode(dto.getOri_filename(), "UTF-8");

			return ResponseEntity.ok()
			                     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
			                     .contentType(MediaType.APPLICATION_OCTET_STREAM)
			                     .body(resource);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 분개 PDF 생성
	@PostMapping("/accountDeptPdf")
	public Map<String, Object> accountDeptPdf(@RequestParam int dept_idx,
	                                          @RequestParam int template_idx) {
		result = new HashMap<>();
		try {
			FileDTO file = service.accountDeptPdf(dept_idx, template_idx);
			result.put("success", true);
			result.put("file_path", "C:/upload/pdf/" + file.getNew_filename());
			result.put("file_idx", file.getFile_idx());
			result.put("filename", file.getOri_filename());
		} catch (Exception e) {
			log.error("분개 PDF 생성 오류", e);
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	}
}
