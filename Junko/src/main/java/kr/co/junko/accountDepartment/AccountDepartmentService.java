package kr.co.junko.accountDepartment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import kr.co.junko.dto.AccountingDepartmentDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.TemplateDTO;
import kr.co.junko.dto.TemplateVarDTO;
import kr.co.junko.template.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountDepartmentService {

	@Autowired
	private final AccountDepartmentDAO dao;
	private final TemplateService templateService;
	
	public List<AccountingDepartmentDTO> accountDeptList(int entry_idx) {
		return dao.accountDeptList(entry_idx);
	}


	public AccountingDepartmentDTO accountDeptDetail(int entry_idx, int dept_idx) {
		return dao.accountDeptDetail(entry_idx, dept_idx);
	}


	public boolean accountDeptAdd(int entry_idx, AccountingDepartmentDTO dto) {
		dto.setEntry_idx(entry_idx);
	    int row = dao.accountDeptAdd(dto);
	    return row > 0;
	}


	public boolean accountDeptUpdate(AccountingDepartmentDTO dto) {
		int row = dao.accountDeptUpdate(dto);
		
		return row > 0;
	}


	public boolean accountDeptDelete(int dept_idx) {
		int row = dao.accountDeptDelete(dept_idx);
		
		return row > 0;
	}


	public void accountDeptFile(MultipartFile file, String string, int dept_idx) throws IllegalStateException, IOException {
		
	    String paths = "c:/upload"; 
	    File files = new File(paths);
	    if (!files.exists()) files.mkdirs();

	    
	    String ori_filename = file.getOriginalFilename();
	    String new_filename = System.currentTimeMillis() + "_" + UUID.randomUUID() + "_" + ori_filename;
	    File ext = new File(files, new_filename);
	    file.transferTo(ext);

	   
	    FileDTO dto = new FileDTO();
	    dto.setOri_filename(ori_filename);
	    dto.setNew_filename(new_filename);
	    dto.setReg_date(LocalDateTime.now());
	    dto.setType("ENTRY_DETAIL");
	    dto.setIdx(dept_idx);
	    dto.setDel_yn(false);

	    dao.accountDeptFile(dto);
		
	}


	public FileDTO deptfileDown(int file_idx) {
		return dao.deptfileDown(file_idx);
	}


	@Transactional
	public FileDTO accountDeptPdf(int dept_idx, int template_idx) throws Exception {
	    // 1. 분개 상세 조회
	    AccountingDepartmentDTO dto = dao.accountDeptDetailByDeptIdx(dept_idx);
	    if (dto == null) throw new IllegalArgumentException("분개 정보 없음");

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
	            case "dept_idx":
	                value = String.valueOf(dto.getDept_idx());
	                break;
	            case "entry_idx":
	                value = String.valueOf(dto.getEntry_idx());
	                break;
	            case "as_name":
	                value = dto.getAs_name();
	                break;
	            case "amount":
	                value = String.valueOf(dto.getAmount());
	                break;
	            case "type":
	                value = dto.getType();
	                break;
	            default:
	                value = "N/A";
	                break;
	        }
	        html = html.replace("{{" + key + "}}", value);
	    }

	    // 4. PDF 경로 설정
	    String uploadRoot = "C:/upload/pdf";
	    new File(uploadRoot).mkdirs();
	    String fileName = "dept_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
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

	    // 6. file 테이블 저장
	    FileDTO file = new FileDTO();
	    file.setOri_filename("분개 PDF");
	    file.setNew_filename(fileName);
	    file.setReg_date(LocalDateTime.now());
	    file.setType("ENTRY_DETAIL"); // 분개 전용
	    file.setIdx(dept_idx);
	    file.setDel_yn(false);
	    dao.accountDeptFile(file);

	    return file;
	}

	
}
