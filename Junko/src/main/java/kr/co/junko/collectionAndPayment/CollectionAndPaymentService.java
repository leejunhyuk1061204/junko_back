package kr.co.junko.collectionAndPayment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID; 

import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import kr.co.junko.dto.CapSearchDTO;
import kr.co.junko.dto.CollectionAndPaymentLogDTO;
import kr.co.junko.dto.CollectionAndPaymentRequestDTO;
import kr.co.junko.dto.CollectionAndPaymentResponseDTO;
import kr.co.junko.dto.CustomDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.LinkedItemDTO;
import kr.co.junko.dto.TemplateDTO;
import kr.co.junko.dto.TemplateVarDTO;
import kr.co.junko.template.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectionAndPaymentService {

	@Autowired
	private final CollectionAndPaymentDAO dao;
	private final TemplateService templateService;
	private final String dir = "c:/upload";
	Map<String, Object> result = null;
	
	public int capRegist(CollectionAndPaymentRequestDTO dto) {
		boolean exites = dao.capRegist(
				dto.getType(),dto.getDate(),dto.getAmount(),dto.getCustom_idx()
				);
		
	if (exites) {
		throw new IllegalArgumentException("동일한 거래 내역이 이미 존재합니다.");
	}
				
		dao.insert(dto);
		return dto.getCap_idx();
		
	}

	public CollectionAndPaymentResponseDTO capList(int cap_idx) {
		return dao.capList(cap_idx);
	}

	public boolean capUpdate(CollectionAndPaymentRequestDTO dto) {
		    boolean exists = dao.capRegist(dto.getType(), dto.getDate(), dto.getAmount(), dto.getCustom_idx());

		    if (exists) return false;

		    dao.capUpdate(dto);
		    return true;
		}

	public boolean capDel(int cap_idx) {
		return dao.capDel(cap_idx) > 0;
	}

	

	public List<CustomDTO> capCustom() {
		 return dao.capCustom();
	}

	public List<CustomDTO> capCustomList() {
		return dao.capCustomList();
	}

	public List<LinkedItemDTO> getEntryList() {
	    return dao.getEntryList();
	}
	public List<LinkedItemDTO> getSettlementList() {
	    return dao.getSettlementList();
	}
	public List<LinkedItemDTO> getInvoiceList() {
	    return dao.getInvoiceList();
	}

	public Map<String, Object> capFile(String string, int cap_idx, MultipartFile file) {
		result = new HashMap<String, Object>();
		
		try {
            String oriName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String ext = oriName.substring(oriName.lastIndexOf("."));
            String newName = uuid + ext;

            Path filePath = Paths.get(dir , newName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());

            FileDTO dto = new FileDTO();
            dto.setOri_filename(oriName);
            dto.setNew_filename(newName);
            dto.setType("collection");
            dto.setIdx(cap_idx);
            dto.setDel_yn(false);
            dto.setReg_date(LocalDateTime.now());

            dao.insertFile(dto);

            result.put("success", true);
            result.put("message", "업로드 성공");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "업로드 실패: " + e.getMessage());
        }

        return result;
	}

	public ResponseEntity<FileSystemResource> capDown(int file_idx) {
		 FileDTO file = dao.getFileByIdx(file_idx);
	        if (file == null) {
	            return ResponseEntity.notFound().build();
	        }

	        Path path = Paths.get(dir + file.getNew_filename());
	        FileSystemResource resource = new FileSystemResource(path.toFile());

	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION,
	                        "attachment; filename=\"" + file.getOri_filename() + "\"")
	                .body(resource);
	}

	public Map<String, Object> capFileDel(int file_idx) {
		result = new HashMap<String, Object>();
		
		 try {
	            dao.deleteFile(file_idx);
	            result.put("success", true);
	            result.put("message", "삭제 완료");
	        } catch (Exception e) {
	            result.put("success", false);
	            result.put("message", "삭제 실패: " + e.getMessage());
	        }

	        return result;
	}

	public List<CollectionAndPaymentLogDTO> getLogsByCapIdx(int cap_idx) {
		return dao.getLogsByCapIdx(cap_idx);
	}

	@Transactional
	public FileDTO capPdf(int cap_idx, int template_idx) throws Exception {
	    // 1. 수금/지급 상세 조회
	    CollectionAndPaymentResponseDTO dto = dao.capList(cap_idx);
	    if (dto == null) throw new IllegalArgumentException("수금/지급 정보 없음");

	    // 2. 템플릿 조회
	    TemplateDTO template = templateService.getTemplate(template_idx);
	    if (template == null) throw new IllegalArgumentException("템플릿 없음");
	    String html = template.getTemplate_html();

	    // 3. 템플릿 변수 치환
	    List<TemplateVarDTO> vars = templateService.templateVarList(template_idx);
	    for (TemplateVarDTO var : vars) {
	        String key = var.getVariable_name();
	        String value;
	        switch (key) {
	            case "cap_idx": value = String.valueOf(dto.getCap_idx()); break;
	            case "type": value = dto.getType(); break;
	            case "date": value = String.valueOf(dto.getDate()); break;
	            case "amount": value = String.valueOf(dto.getAmount()); break;
	            case "customName": value = dto.getCustomName(); break;
	            case "accountBank": value = dto.getAccountBank(); break;
	            case "accountNumber": value = dto.getAccountNumber(); break;
	            case "entryTitle": value = dto.getEntryTitle(); break;
	            case "memo": value = dto.getMemo(); break;
	            default: value = "N/A"; break;
	        }
	        html = html.replace("{{" + key + "}}", value);
	    }

	    // 4. PDF 경로
	    String uploadRoot = "C:/upload/pdf";
	    new File(uploadRoot).mkdirs();
	    String fileName = "cap_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
	    String filePath = Paths.get(uploadRoot, fileName).toString();

	    // 5. PDF 생성
	    try (OutputStream os = new FileOutputStream(filePath)) {
	        PdfRendererBuilder builder = new PdfRendererBuilder();
	        builder.useFastMode();
	        builder.withHtmlContent(html, null);
	        builder.useFont(new File("C:/Windows/Fonts/malgun.ttf"), "malgun");
	        builder.toStream(os);
	        builder.run();
	    }

	    // 6. 파일 테이블 저장
	    FileDTO file = new FileDTO();
	    file.setOri_filename("수금/지급 PDF");
	    file.setNew_filename(fileName);
	    file.setReg_date(LocalDateTime.now());
	    file.setType("collection");
	    file.setIdx(cap_idx);
	    file.setDel_yn(false);
	    dao.insertFile(file);

	    return file;
	}

	public List<CollectionAndPaymentResponseDTO> searchCap(CapSearchDTO dto) {
		return dao.searchCap(dto);
	}

	public int userIdxByLoginId(String loginId) {
		return dao.userIdxByLoginId(loginId);
	}

	public Map<String, Object> searchCapPaged(CapSearchDTO dto) {
	    Map<String, Object> result = new HashMap<>();
	    List<CollectionAndPaymentResponseDTO> list = dao.searchCapPaged(dto);
	    int total = dao.countSearchCap(dto);

	    result.put("list", list);
	    result.put("total", total);
	    return result;
	}

	public int countSearchCap(CapSearchDTO dto) {
	    return dao.countSearchCap(dto);
	}
	
}
