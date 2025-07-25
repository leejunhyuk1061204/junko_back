package kr.co.junko.taxInvoiceFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.TaxInvoiceDTO;
import kr.co.junko.dto.TaxInvoiceDetailDTO;
import kr.co.junko.dto.TemplateDTO;
import kr.co.junko.dto.TemplateVarDTO;
import kr.co.junko.taxInvoice.TaxInvoiceDAO;
import kr.co.junko.taxInvoiceDetail.TaxInvoiceDetailDAO;
import kr.co.junko.template.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaxInvoiceFileService {

	@Autowired
	private final TaxInvoiceFileDAO dao;
	private final TaxInvoiceDAO invoiceDao;
	private final TaxInvoiceDetailDAO invoiceDetailDao;
	private final TemplateService templateService;
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
		Path filePath = Paths.get(paths , dto.getNew_filename());
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

	@Transactional
	public FileDTO taxInvoicePdf(int invoice_idx, int template_idx) throws Exception {
	    // 1. 세금계산서 본문 정보
	    TaxInvoiceDTO invoice = invoiceDao.taxInvoice(invoice_idx);
	    if (invoice == null) throw new IllegalArgumentException("세금계산서 정보 없음");

	    // 2. 품목 목록
	    List<TaxInvoiceDetailDTO> items = invoiceDetailDao.taxProductList(invoice_idx);

	    // 3. 템플릿 조회
	    TemplateDTO template = templateService.getTemplate(template_idx);
	    if (template == null) throw new IllegalArgumentException("템플릿 없음");
	    String html = template.getTemplate_html();

	    // 4. 템플릿 변수 치환 (단일)
	    List<TemplateVarDTO> vars = templateService.templateVarList(template_idx);
	    for (TemplateVarDTO var : vars) {
	        String key = var.getVariable_name();
	        String value;
	        switch (key) {
	            case "invoice_idx": value = String.valueOf(invoice.getInvoice_idx()); break;
	            case "total_amount": value = String.valueOf(invoice.getTotal_amount()); break;
	            case "status": value = invoice.getStatus(); break;
	            case "issued_by": value = invoice.getIssued_by(); break;
	            default: value = "N/A"; break;
	        }
	        html = html.replace("{{" + key + "}}", value);
	    }

	    // 5. 품목 테이블 반복 영역 구성
	    StringBuilder itemHtml = new StringBuilder();
	    for (TaxInvoiceDetailDTO item : items) {
	        itemHtml.append("<tr>")
	                .append("<td>").append(item.getItem_name()).append("</td>")
	                .append("<td>").append(item.getQuantity()).append("</td>")
	                .append("<td>").append(item.getPrice()).append("</td>")
	                .append("<td>").append(item.getTotal_amount()).append("</td>")
	                .append("</tr>");
	    }
	    html = html.replace("{{items}}", itemHtml.toString());

	    // 6. PDF 경로 생성
	    String uploadRoot = "C:/upload/pdf";
	    new File(uploadRoot).mkdirs();
	    String fileName = "tax_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
	    String filePath = Paths.get(uploadRoot, fileName).toString();

	    // 7. PDF 생성
	    try (OutputStream os = new FileOutputStream(filePath)) {
	        PdfRendererBuilder builder = new PdfRendererBuilder();
	        builder.useFastMode();
	        builder.withHtmlContent(html, null);
	        builder.useFont(new File("C:/Windows/Fonts/malgun.ttf"), "malgun");
	        builder.toStream(os);
	        builder.run();
	    }

	    // 8. file 테이블 저장
	    FileDTO file = new FileDTO();
	    file.setOri_filename("세금계산서 PDF");
	    file.setNew_filename(fileName);
	    file.setReg_date(LocalDateTime.now());
	    file.setType("tax_invoice");
	    file.setIdx(invoice_idx);
	    file.setDel_yn(false);
	    dao.taxInvoiceFile(file);

	    return file;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
