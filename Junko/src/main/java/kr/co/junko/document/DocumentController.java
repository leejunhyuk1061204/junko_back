package kr.co.junko.document;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.ApprovalLogDTO;
import kr.co.junko.dto.DocumentCreateDTO;
import kr.co.junko.dto.DocumentDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.TemplatePreviewDTO;
import kr.co.junko.file.FileDAO;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@Slf4j
public class DocumentController {

	@Autowired DocumentService service;
	@Autowired FileDAO filedao;
	
	@Value("${spring.servlet.multipart.location}") 
	private String root;
	
	Map<String, Object> result = null;
	
	// 문서 미리보기
	@PostMapping(value="/document/preview")
	public Map<String, Object> documentPreview(@RequestBody TemplatePreviewDTO dto) {
		result = new HashMap<String, Object>();
		log.info("dto : {}",dto);
		
		String html = service.documentPreview(dto.getTemplate_idx(), dto.getVariables());
		result.put("preview", html);
		result.put("success", html != null);
		
		return result;
	}
	
	// 전자 결재 문서 생성
	@PostMapping(value="/document/insert")
	public Map<String, Object>documentInsert(@RequestBody DocumentCreateDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		
		Map<String, Object> insertResult = service.documentInsert(dto);
		result.putAll(insertResult);
		
		return result;
	}
	
	// 결재 문서 PDF 파일 생성
	@PostMapping(value="/document/pdf")
	public Map<String, Object> documentPDF(
			@RequestBody DocumentDTO dto
			, HttpServletResponse response){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		
		try {
			String filePath = service.documentPDF(dto);
			result.put("success", true);
			result.put(filePath, filePath);			
		} catch (Exception e) {
			result.put("success", false);
		}
		
		return result;
	}
	
	
	// 문서 결재 (승인)
	@PostMapping(value="/document/approve")
	public Map<String, Object> documentApprove(@RequestBody Map<String, Object> req){
		result = new HashMap<String, Object>();
		
		boolean success = service.documentApprove(req);
		result.put("success", success);
		
		return result;
	}
	
	// 문서 결재 (반려)
	@PostMapping(value="/document/reject")
	public Map<String, Object> documentReject(@RequestBody Map<String, Object> req){
		result = new HashMap<String, Object>();
		
		boolean success = service.documentReject(req);
		result.put("success", success);
		
		return result;
	}
	
	// 결재 로그 상세보기
	@GetMapping(value="/document/approval/log/{document_idx}")
	public Map<String, Object> getApprovalLogs(@PathVariable int document_idx){
		result = new HashMap<String, Object>();
		
		List<ApprovalLogDTO> logs = service.getApprovalLogs(document_idx);
		result.put("success", true);
		result.put("logs", logs);
		
		return result;
	}
	
	// docx 파일 생성
	@PostMapping(value="/document/docx")
	public Map<String, Object> documentDOCX(@RequestBody Map<String, Object> req){
		result = new HashMap<String, Object>();
		
		int document_idx = (int) req.get("document_idx");
		String filePath = service.documentDOCX(document_idx);
		
		result.put("success", true);
		result.put("path", filePath);
		result.put("file_url", "/download/docx/" + document_idx);
		
		return result;
	}
	
	// PDF 다운로드
    @GetMapping(value="/download/pdf/{idx}")
    public ResponseEntity<?> downloadPDF(@PathVariable int idx) {
        return sendFile(idx, "pdf", "application/pdf");
    }

    // DOCX 다운로드
    @GetMapping(value="/download/docx/{idx}")
    public ResponseEntity<?> downloadDOCX(@PathVariable int idx) {
        return sendFile(idx, "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }
    
    // 파일 다운로드
    private ResponseEntity<?> sendFile(int idx, String ext, String contentType) {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> param = new HashMap<>();
        param.put("type", "document");
        param.put("idx", idx);
        param.put("ext", ext); // "pdf" or "docx"

        FileDTO file = filedao.selectTypeIdx(param);

        if (file == null || !file.getNew_filename().endsWith("." + ext)) {
            result.put("success", false);
            result.put("message", ext.toUpperCase() + " 파일 정보가 없습니다.");
            return ResponseEntity.status(404).body(result);
        }

        // 업로드된 실제 파일의 경로 만들기
        File actualFile = new File(root+"/" + ext + "/" + file.getNew_filename());
        // 파일 없으면?
        if (!actualFile.exists()) {
            result.put("success", false);
            result.put("message", ext.toUpperCase() + " 파일이 존재하지 않습니다.");
            return ResponseEntity.status(404).body(result);
        }

        // 파일 인코딩 하기 (한글 파일명 인식할 수 있도록)
        try {
            String encodedFilename = URLEncoder.encode(file.getOri_filename() + "." + ext, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");

            // 어떤 파일인지 브라우저에 알려주기
            // PDF 면 application/pdf
            // DOCX 면 application/vnd.openxmlformats-officedocument.wordprocessingml.document
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            
            // Content=Dixposition : 다운로드 하라고 브라우저에 알려주는 헤더
            // attachment : 무조건 다운로드 하게 함.
            // filename : 인코딩된 한글 파일명도 깨지지 않게 함.
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);

            // 실제 파일 Resource 객체로 감싸기
            Resource resource = new FileSystemResource(actualFile);
            
            // 클라이언트로 파일 보내기
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "파일 전송 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(result);
        }
    }
	
}
