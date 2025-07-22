package kr.co.junko.voucher;

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
import kr.co.junko.dto.EntryDetailDTO;
import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.TemplateDTO;
import kr.co.junko.dto.TemplatePreviewDTO;
import kr.co.junko.dto.TemplateVarDTO;
import kr.co.junko.dto.VoucherDTO;
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

    Map<String, Object> result = null;

    // 전표 등록
    @PostMapping("/voucher/insert")
    public Map<String, Object> voucherInsert(
    		@RequestBody VoucherDTO dto){
        log.info("dto : {}", dto);

        result = new HashMap<String, Object>();

        if (dto.getEntry_details() == null || dto.getEntry_details().isEmpty()) {
            service.defaultEntryDetails(dto);
        }

        boolean success = service.voucherInsert(dto);
        result.put("success", success);

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
        result.put("success", success);

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
        log.info("voucherPreview dto: {}", dto);

        // 템플릿 치환 변수 세팅용
        Map<String, String> variables = voucherToVariables(dto.getVariables());

        String html = documentService.documentPreview(dto.getTemplate_idx(), variables);
        result.put("preview", html);
        result.put("success", html != null);
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
        map.put("status", "작성중");

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
    
}
