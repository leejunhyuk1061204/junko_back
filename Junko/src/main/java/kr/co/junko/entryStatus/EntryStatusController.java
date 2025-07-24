package kr.co.junko.entryStatus;

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
import kr.co.junko.dto.CustomDTO;
import kr.co.junko.dto.DocumentDTO;
import kr.co.junko.dto.EntryStatusDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.TemplatePreviewDTO;
import kr.co.junko.file.FileDAO;
import kr.co.junko.member.MemberService;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class EntryStatusController {
    
    @Autowired EntryStatusService service;
    @Autowired DocumentService documentService;
    @Autowired CustomService customService;
    @Autowired MemberService memberService;
    @Autowired FileDAO filedao;

    Map<String, Object> result = null;

    // 정산 등록
    @PostMapping("/settlement/insert")
    public Map<String, Object> settlementInsert(@RequestBody EntryStatusDTO dto) {
        log.info("dto: {}", dto);
        result = new HashMap<>();

        boolean success = service.settlementInsert(dto);
        result.put("success", success);

        if (success) {
            Map<String, String> param = new HashMap<>();
            param.put("settlement_id", String.valueOf(dto.getSettlement_id()));
            param.put("entry_idx", String.valueOf(dto.getEntry_idx()));
            param.put("custom_idx", String.valueOf(dto.getCustom_idx()));
            param.put("settlement_day", dto.getSettlement_day() != null ? dto.getSettlement_day().toString() : "");
            param.put("amount", String.valueOf(dto.getAmount()));
            param.put("total_amount", String.valueOf(dto.getTotal_amount()));
            param.put("status", dto.getStatus() != null ? dto.getStatus() : "작성중");
            param.put("user_idx", String.valueOf(dto.getUser_idx()));

            result.put("variables", settlementToVariables(param));
        }

        return result;
    }   


    // 정산 리스트
    @GetMapping("/settlement/list")
    public Map<String, Object> settlementList(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String from,
        @RequestParam(required = false) String to
    ) {
        result = new HashMap<String, Object>();
        int offset = (page - 1) * size;

        List<EntryStatusDTO> list = service.settlementList(status, keyword, offset, size, from, to);
        int total = service.settlementTotal(status, keyword, from, to);

        result.put("success", true);
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return result;
    }

    // 정산 수정
    @PutMapping("/settlement/update")
    public Map<String, Object> settlementUpdate(@RequestBody EntryStatusDTO dto) {
        log.info("dto: {}", dto);
        result = new HashMap<String, Object>();

        boolean success = service.settlementUpdate(dto);

        result.put("success", success);
        return result;
    }

    // 정산 삭제
    @PutMapping("/settlement/del/{settlement_id}")
    public Map<String, Object> settlementDel(@PathVariable int settlement_id) {
        result = new HashMap<String, Object>();

        boolean success = service.settlementDel(settlement_id);

        result.put("success", success);
        return result;
    }

    // 정산 상세 조회
    @GetMapping("/settlement/detail/{settlement_id}")
    public Map<String, Object> settlementDetail(@PathVariable int settlement_id) {
        result = new HashMap<String, Object>();

        EntryStatusDTO dto = service.settlementDetail(settlement_id);

        if (dto != null) {
            try {
                CustomDTO custom = customService.customSelect(dto.getCustom_idx());
                dto.setCustom_name(custom != null ? custom.getCustom_name() : "");
            } catch (Exception e) {
                dto.setCustom_name("");
            }
            try {
                DocumentDTO document = documentService.getByTypeAndIdx("settlement", settlement_id);
                if (document != null) {
                    dto.setDocument_idx(document.getDocument_idx());
                    result.put("document", document);
                    
                    Map<String, Object> fileParam = new HashMap<>();
                    fileParam.put("type", "document");
                    fileParam.put("idx", settlement_id);
                    fileParam.put("ext", "pdf");

                    FileDTO file = filedao.selectTypeIdx(fileParam);
                    result.put("file", file);
                    
                }
            } catch (Exception e) {
                // 예외 무시 또는 로깅
            }
        }

        result.put("success", dto != null);
        result.put("data", dto);

        return result;
    }

    // 정산 다중 등록
    @PostMapping("/settlement/multi")
    public Map<String, Object> settlementMulti(@RequestBody EntryStatusDTO dto) {
        result = new HashMap<String, Object>();

        boolean success = service.settlementMulti(dto);
        result.put("success", success);

        return result;
    }


    // 정산 미리보기
    @PostMapping("/settlement/preview")
    public Map<String, Object> settlementPreview(@RequestBody TemplatePreviewDTO dto) {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, String> variables = settlementToVariables(dto.getVariables());
        String html = documentService.documentPreview(dto.getTemplate_idx(), variables);

        result.put("preview", html);
        result.put("success", html != null);
        result.put("variables", variables);

        return result;
    }
        
    // 치환 변수 맵
    private Map<String, String> settlementToVariables(Map<String, String> param) {
        Map<String, String> map = new HashMap<>();

        map.put("settlement_idx", param.getOrDefault("settlement_id", "정산 번호 미정"));
        map.put("settlement_day", param.getOrDefault("settlement_day", ""));
        map.put("amount", param.getOrDefault("amount", ""));
        map.put("total_amount", param.getOrDefault("total_amount", ""));
        map.put("status", param.getOrDefault("status", "작성중"));

        try {
            int user_idx = Integer.parseInt(param.getOrDefault("user_idx", "0"));
            MemberDTO user = memberService.selectUserByIdx(user_idx);
            map.put("user_name", user != null ? user.getUser_name() : "");
        } catch (Exception e) {
            map.put("user_name", "");
        }

        try {
            int custom_idx = Integer.parseInt(param.getOrDefault("custom_idx", "0"));
            CustomDTO custom = customService.customSelect(custom_idx);
            map.put("custom_name", custom != null ? custom.getCustom_name() : "");
        } catch (Exception e) {
            map.put("custom_name", "");
        }

        try {
            int entry_idx = Integer.parseInt(param.getOrDefault("entry_idx", "0"));
            int totalAmount = Integer.parseInt(param.getOrDefault("total_amount", "0"));
            int settledSum = service.selectTotalSettlementAmount(entry_idx);
            int unpaid = totalAmount - settledSum;
            map.put("unpaid_amount", String.valueOf(unpaid));
        } catch (Exception e) {
            map.put("unpaid_amount", "");
        }

        return map;
    }

}
