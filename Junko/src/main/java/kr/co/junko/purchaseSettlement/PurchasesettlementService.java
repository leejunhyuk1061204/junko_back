package kr.co.junko.purchaseSettlement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.PurchaseSettlementDTO;
import kr.co.junko.dto.TemplateDTO;
import kr.co.junko.dto.TemplateVarDTO;
import kr.co.junko.template.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchasesettlementService {

	
	@Autowired
	private final PurchasesettlementDAO dao;
	private final TemplateService templateService;
	
	 private final String dir = "C:/upload";
	Map<String, Object> result = null;
	
	public PurchaseSettlementDTO getSettlementById(int settlement_idx) {
		return dao.getSettlementById(settlement_idx);
	}
	
	public Object psRegister(PurchaseSettlementDTO dto) {
		return dao.psRegister(dto);
		
	}
	
	public int settlementUpdate(PurchaseSettlementDTO dto) {
		return dao.settlementUpdate(dto);
	}
	
	public int settlementDel(int settlement_idx) {
		return dao.settlementDel(settlement_idx);
	}
	
	public int settlementFinal(int settlement_idx) {
		return dao.settlementFinal(settlement_idx);
	}
	
	public boolean settlementReq(int settlement_idx) {
		
		PurchaseSettlementDTO dto = dao.getSettlementById(settlement_idx);
	    if (dto == null) return false;

	    // 확정 또는 마감 상태에서만 요청 가능
	    if ("확정".equals(dto.getStatus()) || "마감".equals(dto.getStatus())) {
	        return dao.settlementReq(settlement_idx) > 0;
	    }

	    return false;
	}

	public boolean settlementAdminReq(int settlement_idx) {
		 PurchaseSettlementDTO dto = dao.getSettlementById(settlement_idx);
		    if (dto == null) return false;

		    if ("재정산요청".equals(dto.getStatus())) {
		        return dao.settlementAdminReq(settlement_idx) > 0;
		    }

		    return false;
	}

	public List<FileDTO> settlementFileUpload(int settlement_idx, MultipartFile[] files, String type) throws IllegalStateException, IOException {
		List<FileDTO> savedFiles = new ArrayList();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String ori = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String newName = uuid + "/" + ori;
            String savePath = dir + newName;

            // 실제 파일 저장
            File dest = new File(savePath);
            dest.getParentFile().mkdirs();
            file.transferTo(dest);

            // DTO 만들고 DB 저장
            FileDTO dto = new FileDTO();
            dto.setOri_filename(ori);
            dto.setNew_filename(newName);
            dto.setReg_date(LocalDateTime.now());
            dto.setType(type != null ? type : "기타");
            dto.setIdx(settlement_idx);
            dto.setDel_yn(false);

            dao.settlementFileUpload(dto);
            savedFiles.add(dto);
        }

        return savedFiles;
    }

	public List<FileDTO> settlementFileList(int settlement_idx) {
		return dao.settlementFileList(settlement_idx);
	}

	public int settlementFileDel(int idx) {
		
		return dao.settlementFileDel(idx);
	}

	public FileDTO settlementFileDown(int idx) {
		
		return dao.settlementFileDown(idx);
	}

	@Transactional
	public FileDTO settlementPdf(int settlement_idx, int template_idx) throws Exception {
	    PurchaseSettlementDTO dto = dao.getSettlementById(settlement_idx);
	    if (dto == null) throw new IllegalArgumentException("정산 정보 없음");

	    TemplateDTO template = templateService.getTemplate(template_idx);
	    if (template == null) throw new IllegalArgumentException("템플릿 없음");

	    String html = template.getTemplate_html();

	    // 템플릿 변수 치환
	    List<TemplateVarDTO> vars = templateService.templateVarList(template_idx);
	    for (TemplateVarDTO var : vars) {
	        String key = var.getVariable_name();
	        String value;
	        switch (key) {
	            case "settlement_idx":
	                value = String.valueOf(dto.getSettlement_idx());
	                break;
	            case "entry_idx":
	                value = String.valueOf(dto.getEntry_idx());
	                break;
	            case "custom_idx":
	                value = String.valueOf(dto.getCustom_idx());
	                break;
	            case "settlement_day":
	                value = dto.getSettlement_day();
	                break;
	            case "total_amount":
	                value = String.valueOf(dto.getTotal_amount());
	                break;
	            case "amount":
	                value = String.valueOf(dto.getAmount());
	                break;
	            case "status":
	                value = dto.getStatus();
	                break;
	            default:
	                value = "N/A";
	                break;
	        }

	        html = html.replace("{{" + key + "}}", value);
	    }

	    String dirPath = "C:/upload/pdf";
	    new File(dirPath).mkdirs();
	    String fileName = "settlement_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
	    String filePath = Paths.get(dirPath, fileName).toString();

	    try (OutputStream os = new FileOutputStream(filePath)) {
	        PdfRendererBuilder builder = new PdfRendererBuilder();
	        builder.useFastMode();
	        builder.withHtmlContent(html, null);
	        builder.useFont(new File("C:/Windows/Fonts/malgun.ttf"), "malgun");
	        builder.toStream(os);
	        builder.run();
	    }

	    FileDTO file = new FileDTO();
	    file.setOri_filename("정산 PDF");
	    file.setNew_filename(fileName);
	    file.setReg_date(LocalDateTime.now());
	    file.setType("settlement");
	    file.setIdx(settlement_idx);
	    file.setDel_yn(false);

	    dao.settlementFileUpload(file);

	    return file;
	}

	
	
}
