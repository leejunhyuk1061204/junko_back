package kr.co.junko.accountDepartment;

import java.net.URLEncoder;
import org.springframework.http.HttpHeaders;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
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

import kr.co.junko.dto.AccountingDepartmentDTO;
import kr.co.junko.dto.FileDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AccountDepartmentController {

	@Autowired
	private final AccountDepartmentService service = null;
	Map<String, Object> result = null;
	
	//해당 전표의 분개 리스트 조회
	@GetMapping(value="/accountDeptList/{entry_idx}/detail")
	public Map<String, Object> accountDeptList(@PathVariable int entry_idx){
		result = new HashMap<String, Object>();
		List<AccountingDepartmentDTO> list = service.accountDeptList(entry_idx);
		
		
		result.put("result", "success");
	    result.put("data", list);
	    result.put("count", list.size());
		
		
		return result;
	}
	
	//개별 분개(상세) 조회
	@GetMapping(value="/accountDeptDetail/{entry_idx}/details/{dept_idx}")
	public Map<String, Object> accountDeptDetail(
	        @PathVariable int entry_idx,
	        @PathVariable int dept_idx) {

	    Map<String, Object> result = new HashMap<>();
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
	
	//분개 추가
	@PostMapping(value="/accountDeptAdd/{entry_idx}/details")
	public Map<String, Object> insertDept(
	        @PathVariable int entry_idx,
	        @RequestBody AccountingDepartmentDTO dto) {

	    Map<String, Object> result = new HashMap<>();

	    boolean success = service.accountDeptAdd(entry_idx, dto);

	    if (success) {
	        result.put("result", "success");
	        result.put("message", "분개 등록");
	    } else {
	        result.put("result", "fail");
	        result.put("message", "분개 등록 실패");
	    }

	    return result;
	}

	//분개 수정
	@PutMapping("/accountDeptUpdate/{entry_idx}/details/{dept_idx}")
	public Map<String, Object> accountDeptUpdate(
	        @PathVariable int entry_idx,
	        @PathVariable int dept_idx,
	        @RequestBody AccountingDepartmentDTO dto) {

	    dto.setDept_idx(dept_idx); // URL의 dept_idx를 DTO에 넣어줌

	    boolean success = service.accountDeptUpdate(dto);

	    Map<String, Object> result = new HashMap<>();
	    result.put("result", success ? "success" : "fail");
	    return result;
	}

	//분개 삭제
	@DeleteMapping("/accountDeptDelete/{entry_idx}/details/{dept_idx}")
	public Map<String, Object> accountDeptDelete(
	        @PathVariable int entry_idx,
	        @PathVariable int dept_idx) {

	    boolean success = service.accountDeptDelete(dept_idx);

	    Map<String, Object> result = new HashMap<>();
	    result.put("result", success ? "success" : "fail");
	    return result;
	}

	// 분개 파일 첨부 
	@PostMapping("/accountDeptFile/{entry_idx}/details/{dept_idx}")
	public ResponseEntity<Map<String, Object>> accountDeptFile(
	        @PathVariable int entry_idx,
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
	
	
	// 분개 파일 다운로드
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
	
	
}
