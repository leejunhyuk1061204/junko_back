package kr.co.junko.taxInvoiceFile;

import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
@CrossOrigin
public class TaxInvoiceFileController {

	@Autowired
	private final TaxInvoiceFileService service;
	Map<String, Object> result = null;
	
	// 파일 첨부
	@PostMapping(value="/taxInvoiceFile/{invoice_idx}/upload")
	public Map<String, Object> taxInvoiceFile(@PathVariable int invoice_idx,
		 @RequestParam("file")MultipartFile file){
		
		return service.taxInvoiceFile(invoice_idx,file);
	}
	
	// 파일 리스트 조회
	@GetMapping(value="/invoiceFileList/{invoice_idx}/upload")
	public Map<String, Object> invoiceFileList(@PathVariable int invoice_idx) {
	    return service.invoiceFileList(invoice_idx);
	}
	
	
	// 파일 다운로드
	@GetMapping(value="/invoiceFileDown/{invoice_idx}/upload/{file_idx}")
	public ResponseEntity<UrlResource> invoiceFileDown(@PathVariable int invoice_idx,
            @PathVariable int file_idx) {
		return service.invoiceFileDown(invoice_idx, file_idx);
	}
	
	// 세금계산서 pdf 생
	@PostMapping("/taxInvoicePdf")
	public Map<String, Object> taxInvoicePdf(@RequestParam int invoice_idx, @RequestParam int template_idx) {
	    result = new HashMap<String, Object>();
	    try {
	        FileDTO file = service.taxInvoicePdf(invoice_idx, template_idx);
	        result.put("success", true);
	        result.put("file_path", "C:/upload/pdf/" + file.getNew_filename());
	        result.put("file_idx", file.getFile_idx());
	        result.put("filename", file.getOri_filename());
	    } catch (Exception e) {
	        log.error("세금계산서 PDF 생성 오류", e);
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}

	
	
	
	
	
	
}
