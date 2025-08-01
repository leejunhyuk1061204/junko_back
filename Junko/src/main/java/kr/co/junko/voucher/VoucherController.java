package kr.co.junko.voucher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.custom.CustomService;
import kr.co.junko.document.DocumentService;
import kr.co.junko.dto.ApprovalLineDTO;
import kr.co.junko.dto.CustomDTO;
import kr.co.junko.dto.DocumentCreateDTO;
import kr.co.junko.dto.DocumentDTO;
import kr.co.junko.dto.EntryDetailDTO;
import kr.co.junko.dto.EntryStatusDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.TemplatePreviewDTO;
import kr.co.junko.dto.VoucherDTO;
import kr.co.junko.entryStatus.EntryStatusService;
import kr.co.junko.file.FileDAO;
import kr.co.junko.member.MemberService;
import lombok.extern.slf4j.Slf4j;


@RestController
@CrossOrigin
@Slf4j
public class VoucherController {
    
    @Autowired VoucherService service;
    @Autowired CustomService customService;
    @Autowired DocumentService documentService;
    @Autowired MemberService memberService;
    @Autowired FileDAO fileDAO;
    @Autowired EntryStatusService entryStatusService;

    Map<String, Object> result = null;

    // 전표 등록
    @PostMapping("/voucher/insert")
    public Map<String, Object> voucherInsert(@RequestBody VoucherDTO dto){
        result = new HashMap<>();

        if (dto.getEntry_details() == null || dto.getEntry_details().isEmpty()) {
            service.defaultEntryDetails(dto);
        }

        int entry_idx = service.voucherInsert(dto);

        result.put("success", entry_idx > 0);
        result.put("entry_idx", entry_idx);
        result.put("status", dto.getStatus());

        // 저장 성공 후 → 커밋 완료 후 → 변수 세팅
        if (entry_idx > 0) {
            Map<String, String> param = new HashMap<>();
            param.put("entry_type", dto.getEntry_type());
            param.put("amount", String.valueOf(dto.getAmount()));
            param.put("entry_idx", String.valueOf(entry_idx));
            param.put("user_idx", String.valueOf(dto.getUser_idx()));
            param.put("custom_idx", String.valueOf(dto.getCustom_idx()));
            param.put("entry_date", String.valueOf(dto.getEntry_date()));

            Map<String, String> variables = voucherToVariables(param);
            result.put("variables", variables);
            
            DocumentCreateDTO docDto = new DocumentCreateDTO();
            docDto.setTemplate_idx(dto.getTemplate_idx());
            docDto.setUser_idx(dto.getUser_idx());
            docDto.setVariables(variables);
            docDto.setType("voucher");
            docDto.setIdx(entry_idx);
            docDto.setApprover_ids(dto.getApprover_ids());

            Map<String, Object> docRes = documentService.documentInsert(docDto);
            if (docRes != null && docRes.containsKey("document_idx")) {
                int document_idx = (int) docRes.get("document_idx");
                dto.setDocument_idx(document_idx);
                result.put("document_idx", document_idx);
            }
            
        }
        
        return result;
    }


    // 전표 수정
    @PutMapping("/voucher/update/{entry_idx}")
    public Map<String, Object> voucherUpdate(
            @PathVariable int entry_idx,
            @RequestBody VoucherDTO dto
    ) {
        log.info("dto : {}", dto);

        dto.setEntry_idx(entry_idx);
        result = new HashMap<String, Object>();

        boolean success = service.voucherUpdate(dto);
		
		if (!success) {
		    result.put("success", false);
		}
        
        if (success) {
            Map<String, String> param = new HashMap<>();
            param.put("entry_type", dto.getEntry_type());
            param.put("amount", String.valueOf(dto.getAmount()));
            param.put("entry_idx", String.valueOf(entry_idx));
            param.put("user_idx", String.valueOf(dto.getUser_idx()));
            param.put("custom_idx", String.valueOf(dto.getCustom_idx()));
            param.put("entry_date", String.valueOf(dto.getEntry_date()));

            Map<String, String> variables = voucherToVariables(param);
            result.put("variables", variables);
            
            DocumentCreateDTO docDto = new DocumentCreateDTO();
            docDto.setTemplate_idx(dto.getTemplate_idx());
            docDto.setUser_idx(dto.getUser_idx());
            docDto.setVariables(variables);
            docDto.setType("voucher");
            docDto.setIdx(entry_idx);
            docDto.setDocument_idx(dto.getDocument_idx());
            docDto.setApprover_ids(dto.getApprover_ids());
            
            // 1. 기존 결재라인 삭제
            log.info(">>>>> documentService.delApprovalLines 호출 직전");
            documentService.delApprovalLines(dto.getDocument_idx());

            // 2. 새 결재라인 삽입
            if (dto.getApprover_ids() != null && !dto.getApprover_ids().isEmpty()) {
                int step = 1;
                for (Integer userIdx : dto.getApprover_ids()) {
                    ApprovalLineDTO line = new ApprovalLineDTO();
                    line.setDocument_idx(dto.getDocument_idx());
                    line.setUser_idx(userIdx);
                    line.setStep(step++);
                    line.setStatus("미확인");
                    documentService.insertApprovalLine(line);
                }
            }

            documentService.documentUpdate(docDto);
            result.put("success", true);
            
        }

        return result;
    }

    // 전표 삭제
    @PutMapping("/voucher/del/{entry_idx}")
    public Map<String, Object> voucherDel(@PathVariable int entry_idx) {

        result = new HashMap<String, Object>();
        boolean success = service.voucherDel(entry_idx);
        result.put("success", success);

        return result;
    }


    // 전표 목록
    @GetMapping("/voucher/list")
    public Map<String, Object> voucherList(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String entry_type,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String custom_name,
        @RequestParam(required = false) String custom_owner,
        @RequestParam(required = false) String from,
        @RequestParam(required = false) String to,
        @RequestParam(defaultValue = "entry_date") String sort,
        @RequestParam(defaultValue = "desc") String order
    ) {
        int offset = (page - 1) * size;

        List<VoucherDTO> list = service.voucherList(entry_type, status, keyword, custom_name, custom_owner, from, to, sort, order, offset, size);
        int total = service.voucherTotal(entry_type, status, keyword, custom_name, custom_owner, from, to);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return result;
    }


    // 전표 상세
    @GetMapping("/voucher/detail/{entry_idx}")
    public Map<String, Object> voucherDetail(@PathVariable int entry_idx) {
        result = new HashMap<String, Object>();
        VoucherDTO dto = service.voucherDetail(entry_idx);

    if (dto == null) {
        result.put("success", false);
        return result;
    }

    List<EntryDetailDTO> details = service.entryDetailList(entry_idx);
    dto.setEntry_details(details);
    
    // 문서 조회 (type = voucher, idx = entry_idx)
    DocumentDTO doc = documentService.getByTypeAndIdx("voucher", entry_idx);
    if (doc != null) {
        dto.setDocument_idx(doc.getDocument_idx());
        
        List<ApprovalLineDTO> lines = documentService.getApprovalLines(doc.getDocument_idx());
        List<Integer> approverIds = new ArrayList<>();
        for (ApprovalLineDTO line : lines) {
            approverIds.add(line.getUser_idx());
        }
        dto.setDocument_idx(doc.getDocument_idx());
        result.put("approver_ids", approverIds);
        result.put("approval_lines", lines);

        Map<String, Object> fileParam = new HashMap<>();
        fileParam.put("type", "document");
        fileParam.put("idx", doc.getDocument_idx());
        fileParam.put("ext", "pdf");

        // 파일 조회
        FileDTO file = fileDAO.selectTypeIdx(fileParam);

        if (file != null) {
            dto.setFile_name(file.getNew_filename());
        }
    }
    
    DocumentDTO entryDetailDoc = documentService.getByTypeAndIdx("entry_detail", entry_idx);
    if (entryDetailDoc != null) {
        dto.setEntry_detail_document_idx(entryDetailDoc.getDocument_idx());
    }

    List<EntryStatusDTO> settlementList = entryStatusService.settlementListEntryIdx(entry_idx);
    result.put("settlement_list", settlementList);

        result.put("success", dto != null);
        result.put("data", dto);
        return result;
    }
    
    // 전표 상태 변경
    @PutMapping("/voucher/status/update/{entry_idx}")
    public Map<String, Object> voucherStatusUpdate(
        @PathVariable int entry_idx,
        @RequestBody Map<String, Object> body) {
        result = new HashMap<String, Object>();

        String status = (String) body.get("status");
        boolean success = service.voucherStatusUpdate(entry_idx, status);

        result.put("success", success);
        return result;
    }
    
    @PostMapping("/voucher/preview")
    public Map<String, Object> voucherPreview(@RequestBody TemplatePreviewDTO dto) {
        Map<String, Object> result = new HashMap<>();
        log.info("dto: {}", dto);

        // 템플릿 치환 변수 세팅용
        Map<String, String> variables = voucherToVariables(dto.getVariables());

        String html = documentService.documentPreview(dto.getTemplate_idx(), variables);
        result.put("preview", html);
        result.put("success", html != null);
        result.put("variables", variables);
        return result;
    }

    // 전표 치환 변수 생성 함수
    private Map<String, String> voucherToVariables(Map<String, String> param) {
        Map<String, String> map = new HashMap<>();

        // 전표 정보
        map.put("entry_type", (String) param.getOrDefault("entry_type", ""));
        map.put("entry_date", (String) param.getOrDefault("entry_date", ""));
        map.put("amount", String.valueOf(param.getOrDefault("amount", "")));


        // 전표 번호 - 등록 전이면 '전표 번호 미정'
        map.put("entry_idx", String.valueOf(param.getOrDefault("entry_idx", "전표 번호 미정")));

        // 상태 - 등록 전에는 작성중
        map.put("status", param.getOrDefault("status", "작성중"));

        // 작성자 이름 조회
        Object userIdxObj = param.get("user_idx");
        if (userIdxObj != null) {
            try {
                int user_idx = Integer.parseInt(userIdxObj.toString());
                MemberDTO user = memberService.selectUserByIdx(user_idx);
                if (user != null && user.getUser_name() != null) {
                	map.put("user_name", user.getUser_name());
                }
            } catch (NumberFormatException e) {
            	map.put("user_name", ""); // 파싱 오류 시 비워둠
            }
        }
        
        // 거래처 정보 - DB 조회 필요 (custom_idx 기준)
        Object customIdx = param.get("custom_idx");
        if (customIdx != null) {
            CustomDTO custom = customService.customSelect(Integer.parseInt(customIdx.toString()));
            if (custom != null) {
                map.put("custom_name", custom.getCustom_name());
                map.put("custom_owner", custom.getCustom_owner());
                map.put("custom_phone", custom.getCustom_phone());
            }
        }
        return map;
    }
    
    // 분개 치환 변수 생성 함수
    private Map<String, String> entryDetailToVariables(int entry_idx) {
        Map<String, String> map = new HashMap<>();
        List<EntryDetailDTO> details = service.entryDetailList(entry_idx);

        if (details == null || details.isEmpty()) return map;

        StringBuilder rows = new StringBuilder();
        for (EntryDetailDTO d : details) {
            String asName = service.selectAsNameByIdx(d.getAs_idx());
            rows.append("<tr>")
                .append("<td>").append(d.getDept_idx()).append("</td>")
                .append("<td>").append(asName).append("</td>")
                .append("<td>").append(String.format("%,d", d.getAmount())).append("원</td>")
                .append("<td>").append(d.getType()).append("</td>")
                .append("</tr>");
        }

        map.put("rows", rows.toString());
        map.put("entry_idx", String.valueOf(entry_idx));
        map.put("entry_type", service.selectVoucherType(entry_idx));
        map.put("amount", String.valueOf(service.selectVoucherAmount(entry_idx)));

        return map;
    }

    
    // 분개 문서 미리보기
    @PostMapping("/entry-detail/preview")
    public Map<String, Object> entryDetailPreview(@RequestBody TemplatePreviewDTO dto) {
        Map<String, Object> result = new HashMap<>();
        
        int entry_idx = Integer.parseInt(dto.getVariables().get("entry_idx"));
        Map<String, String> variables = entryDetailToVariables(entry_idx);
        
        String html = documentService.documentPreview(dto.getTemplate_idx(), variables);
        result.put("preview", html);
        result.put("success", html != null);
        result.put("variables", variables);
        return result;
    }

    @GetMapping("/voucher/settled")
    public List<VoucherDTO> getSettledVouchers() {
        return service.getSettledVouchers();
    }
    
    // 수금/지급 안된 것들만 목록 불러오기
    @GetMapping("/voucher/list/receivable")
    public Map<String, Object> getReceivableVouchers(@RequestParam(required = false) String custom_name) {
        List<VoucherDTO> list = service.getReceivableVouchers(custom_name);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("success", true);
        return result;
    }


}
