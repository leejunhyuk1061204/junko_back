package kr.co.junko.file;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class FileController {

	private final FileService service;
	private final FileDAO filedao;
	Map<String, Object> result = null;
	@Value("${spring.servlet.multipart.location}") 
	private String root;
	
	@GetMapping(value="/file/search/{order_idx}")
	public Map<String, Object>fileSearchByOrderIdx(@PathVariable int order_idx){
		log.info("order_idx : "+order_idx);
		result = new HashMap<String, Object>();
		FileDTO file = service.fileSearchByOrderIdx(order_idx);
		if(file != null) {
			result.put("file", file);
			result.put("success", true);
		} else {
			result.put("success", false);
		}
		return result;
	}

    @GetMapping("/pdf/preview/{fileName}")
    public ResponseEntity<Resource> viewPdf(@PathVariable String fileName) throws Exception {
        String filePath = "C:/upload/pdf/"+fileName;
        File file = new File(filePath);
        
        if(!file.exists()) {
        	ResponseEntity.notFound().build();
        }
        Resource resource = new InputStreamResource(new FileInputStream(file));
        
        return ResponseEntity.ok().
        		header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""+fileName+"\"").
        		contentType(MediaType.APPLICATION_PDF).
        		body(resource);
    }
    
    // PDF 다운로드
    @GetMapping(value="/download/pdf")
    public ResponseEntity<?> downloadPDF(@RequestParam String idx, String type) {
        return sendFile(Integer.parseInt(idx), "pdf", "application/pdf",type);
    }

    // DOCX 다운로드
    @GetMapping(value="/download/docx")
    public ResponseEntity<?> downloadDOCX(@RequestParam String idx, String type) {
        return sendFile(Integer.parseInt(idx), "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",type);
    }
    
    // 파일 다운로드
    private ResponseEntity<?> sendFile(int idx, String ext, String contentType, String type) {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> param = new HashMap<>();
        param.put("type", type);
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
	

    // 파일 저장
    @PostMapping("/file/upload/{type}/{idx}")
    public Map<String, Object> uploadFile(
            @PathVariable String type,
            @PathVariable int idx,
            @RequestParam("file") MultipartFile file) {

        result = new HashMap<String, Object>();

        boolean success = service.uploadFile(type, idx, file);

        result.put("success", success);
        return result;
    }

    // 파일 리스트 조회
    @GetMapping("/file/list/{type}/{idx}")
    public Map<String, Object> getFileList(
        @PathVariable String type,
        @PathVariable int idx) {

        result = new HashMap<>();
        List<FileDTO> list = service.fileList(type, idx);
        result.put("success", true);
        result.put("list", list);
        return result;
    }

    // 파일 삭제
    @PutMapping("/file/del/{type}/{idx}")
    public Map<String, Object> delFile(
        @PathVariable String type,
        @PathVariable int idx) {

        result = new HashMap<>();
        boolean success = service.delFile(type, idx);
        result.put("success", success);
        return result;
    }

    // file_idx 로 다운로드
    @GetMapping("/download/file/{file_idx}")
    public ResponseEntity<?> downloadFileIdx(@PathVariable int file_idx) {
        return service.downloadFileFileIdx(file_idx, "pdf", "application/pdf");
    }
    
    // 가장 최근 pdf
    @GetMapping("/file/latest/pdf")
    public Map<String, Object> latestPdfFile(
        @RequestParam String type,
        @RequestParam int idx) {

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("type", type);
        param.put("idx", idx);
        param.put("ext", "pdf");

        FileDTO file = filedao.latestPdfFile(param);

        if (file != null) {
            result.put("success", true);
            result.put("file", file);
        } else {
            result.put("success", false);
        }

        return result;
    }


}
