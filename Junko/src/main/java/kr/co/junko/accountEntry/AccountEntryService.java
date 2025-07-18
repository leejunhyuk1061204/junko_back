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

import kr.co.junko.dto.AccountingDepartmentDTO;
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
		result.put("total", dao.accountTotalCount());
		
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
	    boolean result = dao.accountUpdate(entry_idx, dto, user_id);
	    
	    if (result) {
	        //기존 PDF 삭제
	        dao.deletePdfByEntryIdx(entry_idx);

	        // 새 PDF 생성
	        try {
	            accountPdf(entry_idx, 10);
	        } catch (Exception e) {
	            log.error("❌ 수정 후 PDF 생성 실패", e);
	            throw new RuntimeException("수정은 되었으나 PDF 재생성 실패");
	        }
	    }
	    
	    return result;
	}

	public boolean accountDelete(int entry_idx, String user_id) {
	    // 1. PDF 파일 삭제
	    try {
	        FileDTO pdfFile = dao.getPdfFileByEntryIdx(entry_idx);
	        if (pdfFile != null) {
	            String path = "C:/upload/pdf/" + pdfFile.getNew_filename();
	            File file = new File(path);
	            if (file.exists()) file.delete(); // 실제 파일 삭제
	            dao.deletePdfPhysicallyByEntryIdx(entry_idx); // DB에서도 삭제
	        }
	    } catch (Exception e) {
	        log.error("PDF 삭제 중 오류", e);
	    }

	    // 2. 전표 논리삭제
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
	    Map<String, Object> data = dao.accountDetail(entry_idx);
	    if (data == null || data.isEmpty()) {
	        throw new IllegalArgumentException("전표 정보가 없습니다.");
	    }

	    TemplateDTO template = templateService.getTemplate(template_idx);
	    if (template == null) {
	        throw new IllegalArgumentException("템플릿이 존재하지 않습니다.");
	    }

	    String html = template.getTemplate_html();
	    List<TemplateVarDTO> varList = templateService.templateVarList(template_idx);

	    for (TemplateVarDTO var : varList) {
	        String key = var.getVariable_name();
	        String value = String.valueOf(data.getOrDefault(key, "N/A"));
	        html = html.replace("{{" + key + "}}", value);
	    }

	    // ✅ PDF 저장 경로 처리
	    String uploadRoot = "C:/upload/pdf";
	    new File(uploadRoot).mkdirs();
	    String fileName = "account_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
	    String filePath = Paths.get(uploadRoot, fileName).toString();

	    try (OutputStream os = new FileOutputStream(filePath)) {
	        PdfRendererBuilder builder = new PdfRendererBuilder();
	        builder.useFastMode();
	        builder.withHtmlContent(html, null);
	        builder.useFont(new File("C:/Windows/Fonts/malgun.ttf"), "malgun");
	        builder.toStream(os);
	        builder.run();
	    }

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
	
	public Integer findSalesIdxByName(String name) {
	    Integer idx = dao.findSalesIdxByName(name);
	    if (idx == null) {
	        log.warn("❗ 고객명 '{}'에 해당하는 sales_idx 없음", name);
	    }
	    return idx;
	}

	public Integer findCustomIdxByName(String name) {
	    Integer idx = dao.findCustomIdxByName(name);
	    if (idx == null) {
	        log.warn("❗ 거래처명 '{}'에 해당하는 custom_idx 없음", name);
	    }
	    return idx;
	}

	@Transactional
	public void insertAccountingEntry(AccountingEntryDTO dto, MultipartFile file) {
	    // 1. 전표 등록
	    dao.accountRegist(dto); // entry_idx 생성됨

	    // 2. 첨부파일 처리
	    if (file != null && !file.isEmpty()) {
	        try {
	        	String ori = file.getOriginalFilename();
	        	String ext = ori.substring(ori.lastIndexOf('.'));
	        	String uuid = UUID.randomUUID().toString();
	        	String newName = uuid + ext;

	            String uploadPath = "C:/upload";
	            File uploadDir = new File(uploadPath);
	            if (!uploadDir.exists()) uploadDir.mkdirs();

	            File saveFile = Paths.get(uploadPath, newName).toFile();
	            file.transferTo(saveFile); 

	            FileDTO fileDTO = new FileDTO();
	            fileDTO.setOri_filename(ori);
	            fileDTO.setNew_filename(newName);
	            fileDTO.setReg_date(LocalDateTime.now());
	            fileDTO.setType("accounting");
	            fileDTO.setIdx(dto.getEntry_idx());
	            fileDTO.setDel_yn(false);

	            dao.accountFile(fileDTO);
	        } catch (Exception e) {
	            throw new RuntimeException("파일 저장 실패", e); // 예외 발생 시 전체 롤백
	        }
	    }

	    // 3. PDF 자동 생성 (필수)
	    try {
	        log.info("✅ 전표 등록 완료, PDF 생성 시도");
	        int defaultTemplateIdx = 10;
	        FileDTO pdf = accountPdf(dto.getEntry_idx(), defaultTemplateIdx);

	        if (pdf == null) {
	            throw new RuntimeException("PDF 생성 실패: 결과 파일이 null");
	        }
	    } catch (Exception e) {
	        log.error("❌ 전표 PDF 자동 생성 실패", e);
	        throw new RuntimeException("PDF 생성 중 오류", e); // 전체 트랜잭션 롤백
	    }
	}

	public boolean approveEntry(int entry_idx, int user_idx) {
		// 상태 변경: 작성중 → 승인됨
	    int updated = dao.updateEntryStatus(entry_idx, "승인됨");

	    // 로그 저장
	    if (updated > 0) {
	        AccountingEntryLogDTO log = new AccountingEntryLogDTO();
	        log.setEntry_idx(entry_idx);
	        log.setUser_id(dao.getUserIdByIdx(user_idx));
	        log.setAction("승인");
	        log.setBefore_status("작성중"); // 실제 이전 상태를 조회해도 좋음
	        log.setAfter_status("승인됨");
	        log.setLog_message("관리자가 승인함");
	        log.setCreated_at(LocalDateTime.now());

	        dao.saveLog(log);
	        return true;
	    }

	    return false;
	}

	public boolean isBalanced(int entry_idx) {
		 List<AccountingDepartmentDTO> deptList = dao.accountDeptList(entry_idx);
		    int debitSum = 0;
		    int creditSum = 0;

		    for (AccountingDepartmentDTO dept : deptList) {
		        if ("차변".equals(dept.getType())) {
		            debitSum += dept.getAmount();
		        } else if ("대변".equals(dept.getType())) {
		            creditSum += dept.getAmount();
		        }
		    }

		    return debitSum == creditSum;
		}

	public boolean hasDept(int entry_idx) {
		int count = dao.countDeptByEntryIdx(entry_idx);
	    return count > 0;
	}




	

}
