package kr.co.junko.accountEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import kr.co.junko.accountDepartment.AccountDepartmentDAO;
import kr.co.junko.dto.*;
import kr.co.junko.template.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountEntryService {

    @Autowired
    private final AccountEntryDAO dao;
    private final AccountDepartmentDAO deptDao;
    private final TemplateService templateService;
    private int limit = 10, page = 0;

    public Map<String, Object> accountList(String page) {
        Map<String, Object> result = new HashMap<>();
        this.page = Integer.parseInt(page);
        int offset = (this.page - 1) * limit;
        result.put("page", this.page);
        result.put("list", dao.accountList(offset, limit));
        result.put("pages", dao.pages(limit));
        result.put("total", dao.accountTotalCount());
        return result;
    }

    public boolean accountRegist(AccountingEntryDTO dto) {
        return dao.accountRegist(dto) > 0;
    }

    public Map<String, Object> accountDetail(int entry_idx) {
        return dao.accountDetail(entry_idx);
    }

    public boolean accountUpdate(int entry_idx, AccountingEntryDTO dto, String user_id) {
        boolean result = dao.accountUpdate(entry_idx, dto, user_id);
        if (result) {
            dao.deletePdfByEntryIdx(entry_idx);
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
        try {
            FileDTO pdfFile = dao.getPdfFileByEntryIdx(entry_idx);
            if (pdfFile != null) {
                File file = new File("C:/upload/pdf/" + pdfFile.getNew_filename());
                if (file.exists()) file.delete();
                dao.deletePdfPhysicallyByEntryIdx(entry_idx);
            }
        } catch (Exception e) {
            log.error("PDF 삭제 중 오류", e);
        }
        return dao.accountDelete(entry_idx, user_id);
    }

    public void accountStatusUpdate(int entry_idx, String newStatus, int user_idx, String logMsg) {
        Map<String, Object> entryInfo = dao.getEntryWriterAndStatus(entry_idx);
        Integer writerIdx = (Integer) entryInfo.get("user_idx");
        String beforeStatus = (String) entryInfo.get("status");

        if (writerIdx == null || user_idx != writerIdx) {
            throw new RuntimeException("작성자만 상태를 변경할 수 있습니다.");
        }

        if (!hasDept(entry_idx)) {
            throw new RuntimeException("분개가 1건 이상 등록되어야 상태 변경이 가능합니다.");
        }

        if (!isBalanced(entry_idx)) {
            throw new RuntimeException("차변과 대변의 금액이 일치하지 않아 상태 변경이 불가능합니다.");
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
        Map<String, Object> result = new HashMap<>();
        try {
            String ori_filename = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String ext = ori_filename.substring(ori_filename.lastIndexOf('.'));
            String new_filename = uuid + ext;
            String path = "C:/upload";

            file.transferTo(new File(path, new_filename));

            FileDTO dto = new FileDTO();
            dao.accountFile(dto);
            result.put("success", true);
            result.put("dto", dto);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    public Map<String, Object> entryFileList(int entry_idx) {
        List<FileDTO> dto = dao.entryFileList(entry_idx);
        Map<String, Object> result = new HashMap<>();
        result.put("files", dto);
        result.put("count", dto.size());
        result.put("success", true);
        return result;
    }

    public FileDTO entryFileDown(int file_idx) {
        return dao.entryFileDown(file_idx);
    }

    public Map<String, Object> accountLog(int entry_idx) {
        List<AccountingEntryLogDTO> dto = dao.accountLog(entry_idx);
        Map<String, Object> result = new HashMap<>();
        result.put("dto", dto);
        result.put("count", dto.size());
        result.put("success", true);
        return result;
    }

    @Transactional
    public FileDTO accountPdf(int entry_idx, int template_idx) throws Exception {
        Map<String, Object> data = dao.accountDetail(entry_idx);
        TemplateDTO template = templateService.getTemplate(template_idx);
        List<TemplateVarDTO> varList = templateService.templateVarList(template_idx);

        String html = template.getTemplate_html();
        for (TemplateVarDTO var : varList) {
            String key = var.getVariable_name();
            String value = String.valueOf(data.getOrDefault(key, "N/A"));
            html = html.replace("{{" + key + "}}", value);
        }

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
        file.setOri_filename(template.getTemplate_name() + "_전표");
        file.setNew_filename(fileName);
        file.setIdx(entry_idx);
        file.setType("entry");
        file.setDel_yn(false);
        file.setReg_date(LocalDateTime.now());
        dao.accountPdf(file);
        return file;
    }

    public Map<String, Object> accountListSearch(AccountingEntrySearchDTO dto) {
        int offset = (dto.getPage() - 1) * dto.getLimit();
        dto.setPage(offset);

        List<AccountingEntryDTO> list = dao.accountListSearch(dto);
        int total = dao.accountListSearchCount(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", offset / dto.getLimit() + 1);
        result.put("pages", (int) Math.ceil((double) total / dto.getLimit()));
        return result;
    }

    public int userIdxByLoginId(String loginId) {
        return dao.userIdxByLoginId(loginId);
    }

    public Integer findSalesIdxByName(String name) {
        Integer idx = dao.findSalesIdxByName(name);
        if (idx == null) log.warn("❗ 고객명 '{}'에 해당하는 sales_idx 없음", name);
        return idx;
    }

    public Integer findCustomIdxByName(String name) {
        Integer idx = dao.findCustomIdxByName(name);
        if (idx == null) log.warn("❗ 거래처명 '{}'에 해당하는 custom_idx 없음", name);
        return idx;
    }

    @Transactional
    public int insertAccountingEntry(AccountingEntryDTO dto, MultipartFile file) {
        dao.accountRegist(dto);
        int entry_idx = dto.getEntry_idx();

        if (file != null && !file.isEmpty()) {
            try {
                String ori = file.getOriginalFilename();
                String ext = ori.substring(ori.lastIndexOf('.'));
                String uuid = UUID.randomUUID().toString();
                String newName = uuid + ext;

                String uploadPath = "C:/upload";
                File saveFile = Paths.get(uploadPath, newName).toFile();
                file.transferTo(saveFile);

                FileDTO fileDTO = new FileDTO();
                fileDTO.setOri_filename(ori);
                fileDTO.setNew_filename(newName);
                fileDTO.setType("account");
                fileDTO.setIdx(entry_idx);
                dao.accountFile(fileDTO);
            } catch (Exception e) {
                throw new RuntimeException("파일 저장 실패", e);
            }
        }

        try {
            log.info("✅ 전표 등록 완료, PDF 생성 시도");
            FileDTO pdf = accountPdf(dto.getEntry_idx(), 10);
            if (pdf == null) throw new RuntimeException("PDF 생성 실패: 결과 파일이 null");
        } catch (Exception e) {
            log.error("❌ 전표 PDF 자동 생성 실패", e);
            throw new RuntimeException("PDF 생성 중 오류", e);
        }
		return entry_idx;
    }

    public boolean approveEntry(int entry_idx, int user_idx) {
        int updated = dao.updateEntryStatus(entry_idx, "승인됨");
        if (updated > 0) {
            AccountingEntryLogDTO log = new AccountingEntryLogDTO();
            log.setEntry_idx(entry_idx);
            log.setUser_id(dao.getUserIdByIdx(user_idx));
            log.setAction("승인");
            log.setBefore_status("작성중");
            log.setAfter_status("승인됨");
            log.setLog_message("관리자가 승인함");
            log.setCreated_at(LocalDateTime.now());
            dao.saveLog(log);
            return true;
        }
        return false;
    }

    public boolean isBalanced(int entry_idx) {
        List<AccountingDepartmentDTO> deptList = deptDao.accountDeptList(entry_idx);
        int debitSum = 0;
        int creditSum = 0;
        for (AccountingDepartmentDTO dept : deptList) {
            if ("차변".equals(dept.getType())) debitSum += dept.getAmount();
            else if ("대변".equals(dept.getType())) creditSum += dept.getAmount();
        }
        return debitSum == creditSum;
    }

    public boolean hasDept(int entry_idx) {
        return deptDao.countDeptByEntryIdx(entry_idx) > 0;
    }

    public List<AccountingEntryDTO> getEntryListForSettlement() {
        List<AccountingEntryDTO> all = dao.accountList(0, 99999); // 충분히 큰 수
        return all.stream()
                .filter(e -> "미정산".equals(e.getStatus()) || "부분정산".equals(e.getStatus()))
                .collect(Collectors.toList());
    }
}
