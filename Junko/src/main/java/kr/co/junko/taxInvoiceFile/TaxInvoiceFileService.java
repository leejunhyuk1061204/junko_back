package kr.co.junko.taxInvoiceFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaxInvoiceFileService {

	@Autowired
	private final TaxInvoiceFileDAO dao;
	Map<String, Object> result = null;
	
	public Map<String, Object> taxInvoiceFile(int invoice_idx, MultipartFile file) {
		result = new HashMap<String, Object>();
		
		
		try {
	        String paths = "C:/upload"; 
	        String ori_filename = file.getOriginalFilename();
	        String uuid = UUID.randomUUID().toString();
	        String new_filename = uuid + "/" + ori_filename;

	        File destFile = new File(paths + new_filename);
	        file.transferTo(destFile); // 실제 파일 저장

	        FileDTO dto = new FileDTO();
	        dto.setIdx(invoice_idx);  
	        dto.setOri_filename(ori_filename);
	        dto.setNew_filename(new_filename);
	        dto.setType("tax_invoice"); 
	        dto.setReg_date(LocalDateTime.now());
	        dto.setDel_yn(false);

	        dao.taxInvoiceFile(dto);

	        result.put("success", true);
	        result.put("message", "파일이 업로드되었습니다.");
	        result.put("file_idx", dto.getFile_idx());
	    } catch (Exception e) {
	        e.printStackTrace();
	        result.put("success", false);
	        result.put("message", "파일 업로드 중 오류 발생: " + e.getMessage());
	    }

	    return result;
	}

	public Map<String, Object> invoiceFileList(int invoice_idx) {
		result = new HashMap<String, Object>();
		
		List<FileDTO> dto = dao.invoiceFileList(invoice_idx, "tax_invoice");

	    if (dto == null || dto.isEmpty()) {
	        result.put("success", false);
	        result.put("message", "첨부파일이 없습니다.");
	    } else {
	        result.put("success", true);
	        result.put("dto", dto);
	    }

	    return result;
	}

	public ResponseEntity<UrlResource> invoiceFileDown(int invoice_idx, int file_idx) {
		
		FileDTO dto = dao.invoiceFileDown(file_idx);
		
		if(dto == null || dto.isDel_yn() || dto.getIdx() != invoice_idx || !"tax_invoice".equals(dto.getType())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		try {
		String paths = "C:/upload";
		Path filePath = Paths.get(paths + dto.getNew_filename());
		UrlResource resource = new UrlResource(filePath.toUri());
		
		if (!resource.exists()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			
		}
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(dto.getOri_filename(), "UTF-8") + "\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
		
		}catch (Exception e) {
			 e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
