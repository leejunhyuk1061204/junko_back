package kr.co.junko.accountEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import kr.co.junko.dto.AccountingEntryDTO;
import kr.co.junko.dto.AccountingEntryLogDTO;
import kr.co.junko.dto.AccountingEntrySearchDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.TemplateDTO;
import kr.co.junko.dto.TemplateVarDTO;
import kr.co.junko.template.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountEntryService {
	
	@Autowired
	private final AccountEntryDAO dao;
	private final TemplateService templateService;
	private int limit = 10 , page = 0; //1페이지당 뜨는 리스트 10개
	
	public Map<String, Object> accountList(String page) {
		Map<String, Object> result = new HashMap<String, Object>();
		this.page=Integer.parseInt(page);
		result.put("page", this.page);
		int offset = (this.page-1) * limit;
		result.put("list", dao.accountList(offset,limit));
		result.put("pages", dao.pages(limit));
		
		return result;
	}

	public boolean accountRegist(AccountingEntryDTO dto) {
		int row = dao.accountRegist(dto);
		return row > 0 ? true : false;
	}

	public Map<String, Object> accountDetail(int entry_idx) {
		return dao.accountDetail(entry_idx);
	}

	public boolean accountUpdate(int entry_idx, AccountingEntryDTO dto, String user_id) {
		return dao.accountUpdate(entry_idx,dto,user_id);
	}

	public boolean accountDelete(int entry_idx, String user_id) {
		return dao.accountDelete(entry_idx, user_id);
	}

	public void accountStatusUpdate(int entry_idx, String newStatus, int user_idx, String logMsg) {
		Map<String, Object> entryInfo = dao.getEntryWriterAndStatus(entry_idx);
		Integer writerIdx = (Integer) entryInfo.get("user_idx");
		String beforeStatus = (String) entryInfo.get("status");
		
		if (writerIdx == null || user_idx != writerIdx) {
			throw new RuntimeException("작성자만 상태를 변경할 수 있습니다.");
		}
		
		dao.accountStatusUpdate(entry_idx, newStatus);
		
		AccountingEntryLogDTO dto = new AccountingEntryLogDTO();
		dto.setEntry_idx(entry_idx);
		dto.setUser_idx(user_idx);
		dto.setAction("상태변경");
		dto.setBefore_status(beforeStatus);
		dto.setAfter_status(newStatus);
		dto.setLog_message(logMsg);
		dto.setCreated_at(LocalDateTime.now());
	    dao.saveLog(dto);
		
		
	}

	public Map<String, Object> accountFile(int entry_idx, MultipartFile file) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			String ori_filename = file.getOriginalFilename();
			String uuid = UUID.randomUUID().toString();
			String ext = ori_filename.substring(ori_filename.lastIndexOf('.'));
			String new_filename = uuid + ext;
			String paths = "C:/upload"; 
			
			File saveFile = new File(paths, new_filename);
			file.transferTo(saveFile);
			
			FileDTO dto = new FileDTO();
			dto.setOri_filename(ori_filename);
			dto.setNew_filename(new_filename);
			dto.setReg_date(LocalDateTime.now());
			dto.setType("accounting");
			dto.setIdx(entry_idx);
			dto.setDel_yn(false);
			
			dao.accountFile(dto);
			
			result.put("success", true);
	        result.put("dto", dto);
			
		}catch (Exception e) {
			result.put("success", false);
	        result.put("message", e.getMessage());
		}
		
		
		return result;
	}


	public Map<String, Object> entryFileList(int entry_idx) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<FileDTO> dto = dao.entryFileList(entry_idx);
		
		result.put("files", dto);
		result.put("count", dto.size());
		result.put("success", true);
		
		
		return result;
	}

	public FileDTO entryFileDown(int file_idx) {
		
		return dao.entryFileDown(file_idx);
	}

	public Map<String, Object> accountLog(int entry_idx) {
		Map<String, Object> result = new HashMap<String, Object>();
		    List<AccountingEntryLogDTO> dto = dao.accountLog(entry_idx);
		    result.put("dto", dto);
		    result.put("count", dto.size());
		    result.put("success", true);
		    return result;
	}

	@Transactional
	public FileDTO accountPdf(int entry_idx, int template_idx) throws Exception {
	    // 전표 상세 정보 조회
	    Map<String, Object> data = dao.accountDetail(entry_idx);
	    if (data == null || data.isEmpty()) {
	        throw new IllegalArgumentException("전표 정보가 없습니다.");
	    }

	    // 템플릿 조회
	    TemplateDTO template = templateService.getTemplate(template_idx);
	    if (template == null) {
	        throw new IllegalArgumentException("템플릿이 존재하지 않습니다.");
	    }

	    String html = template.getTemplate_html();

	    // 변수 치환
	    List<TemplateVarDTO> varList = templateService.templateVarList(template_idx);
	    log.info(("치환 전 html: \n" + html));
	    for (TemplateVarDTO var : varList) {
	        String key = var.getVariable_name();
	        String value = String.valueOf(data.getOrDefault(key, "N/A"));
	        log.info(("치환 변수: " + key + " = " + value));
	        html = html.replace("{{" + key + "}}", value);
	    }
	    log.info("치환 후 html: \n" + html);
	    // PDF 경로 설정
	    String uploadRoot = "c:/upload/pdf";
	    new File(uploadRoot).mkdirs();
	    String fileName = "account_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
	    String filePath = Paths.get(uploadRoot, fileName).toString();

	    // PDF 생성
	    try (OutputStream os = new FileOutputStream(filePath)) {
	        PdfRendererBuilder builder = new PdfRendererBuilder();
	        builder.useFastMode();
	        builder.withHtmlContent(html, null);
	        builder.useFont(new File("C:/Windows/Fonts/malgun.ttf"), "malgun");
	        builder.toStream(os);
	        builder.run();
	    }

	    // 파일 테이블 저장
	    FileDTO file = new FileDTO();
	    file.setOri_filename("전표 PDF");
	    file.setNew_filename(fileName);
	    file.setReg_date(LocalDateTime.now());
	    file.setType("pdf");
	    file.setIdx(entry_idx);
	    file.setDel_yn(false);
	    dao.accountPdf(file);
	    
	    
	    return file;
	}

	public Map<String, Object> accountListSearch(AccountingEntrySearchDTO dto) {
		Map<String, Object> result = new HashMap<String, Object>();

		int offset = (dto.getPage() - 1) * dto.getLimit();
		dto.setPage(offset); // offset 처리

		List<AccountingEntryDTO> list = dao.accountListSearch(dto);
		int total = dao.accountListSearchCount(dto);

		result.put("list", list);
		result.put("total", total);
		result.put("page", dto.getPage() / dto.getLimit() + 1);
		result.put("pages", (int)Math.ceil((double) total / dto.getLimit()));
		return result;
	}

	public int userIdxByLoginId(String loginId) {
		return dao.userIdxByLoginId(loginId);
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
