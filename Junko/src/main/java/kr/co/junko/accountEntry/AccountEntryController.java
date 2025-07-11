package kr.co.junko.accountEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.AccountingEntryDTO;
import kr.co.junko.dto.FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
@CrossOrigin
public class AccountEntryController {
	
	@Autowired
	private final AccountEntryService service;
	Map<String, Object> result = null;
	
	// 전표 리스트 페이징
	@GetMapping(value="accountList/{page}")
	public Map<String, Object> accountList(@PathVariable String page){

		result = new HashMap<String, Object>();
		result = service.accountList(page);
		return result;
	}
	
	// 전표 등록
	@PostMapping(value="/accountRegist")
	public Map<String, Object> accountRegist(@RequestBody AccountingEntryDTO dto){
		result = new HashMap<String, Object>();
		boolean success = service.accountRegist(dto);
		result.put("success", success);
		
		
		return result;
	}
	
	// 전표 상세조회 
	@GetMapping(value="/accountDetail/{entry_idx}")
	public Map<String, Object> accountDetail(@PathVariable int entry_idx){
		return service.accountDetail(entry_idx);
	}
	
	// 전표 수정
	@PutMapping(value="/accountUpdate/{entry_idx}")
	public Map<String, Object> accountUpdate(@PathVariable int entry_idx,
			@RequestBody AccountingEntryDTO dto,
            @RequestParam String user_id){
		
		result = new HashMap<String, Object>();
		boolean success = service.accountUpdate(entry_idx, dto, user_id);
		result.put("success", success);
		
		return result;
	}
	
	// 전표 삭제
	@DeleteMapping(value="/accountDelete/{entry_idx}")
	public Map<String, Object> accountDelete(@PathVariable int entry_idx,
			 @RequestParam String user_id){
		
		result = new HashMap<String, Object>();
		boolean success = service.accountDelete(entry_idx,user_id);
		result.put("success", success);
		
		return result;
	}
	
	//전표 상태 변경
	@PatchMapping(value="/accountStatusUpdate/{entry_idx}/status")
	public ResponseEntity<?> accountStatusUpdate(@PathVariable int entry_idx,
			@RequestBody Map<String, String> map,
			@RequestParam String user_id){
		
		String newStatus = map.get("status");
		String logMsg = map.getOrDefault("logMsg", null);
		service.accountStatusUpdate(entry_idx, newStatus, user_id, logMsg);
		
		
		return ResponseEntity.ok().build();
		
	}
	
	// 전표 증빙파일 첨부 등록
	@PostMapping(value="/accountFile/{entry_idx}/upload")
	public Map<String, Object> accountFile(@PathVariable int entry_idx,
		 @RequestParam("file") MultipartFile file){
		
		return service.accountFile(entry_idx,file);
	}
	
	// 전표 증빙파일 리스트 / 다운로드
	@GetMapping(value="/entryFileList/{entry_idx}/upload")
	public Map<String, Object> entryFileList(@PathVariable int entry_idx) {
	    return service.entryFileList(entry_idx);
	}
	
	@GetMapping(value="/entryFileDown/{file_idx}")
	public ResponseEntity<InputStreamResource> entryFileDown(@PathVariable int file_idx) throws IOException{
		
		FileDTO dto = service.entryFileDown(file_idx);
		File file = new File("C:/upload" + dto.getNew_filename());
		
		if (!file.exists()) {
			throw new FileNotFoundException("존재하지 않 파일");
		}
		
	
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
	    return ResponseEntity.ok()
	            .header("Content-Disposition", "attachment; filename=\"" + dto.getOri_filename() + "\"")
	            .body(resource);
	}
	
	
	// 전표 변경 이력 및 상태 변경 로그 조회 
	@GetMapping(value="/accountLog/{entry_idx}")
	public Map<String, Object> accountLog(@PathVariable int entry_idx) {
	    return service.accountLog(entry_idx);
	}
	
	// 전표 pdf 생성
	@PostMapping(value="/accountPdf")
	public Map<String, Object> accountPdf(@RequestParam int entry_idx, @RequestParam int template_idx) {
	    result = new HashMap<String, Object>();
	    try {
	    	FileDTO file = service.accountPdf(entry_idx, template_idx);
	    	result.put("file_path", "C:/upload/pdf/" + file.getNew_filename());
	    	result.put("file_idx", file.getFile_idx());
	    	result.put("filename", file.getOri_filename());
	    } catch (Exception e) {
	        log.error("전표 PDF 생성 오류", e);
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
