package kr.co.junko.option;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.CombinedDTO;
import kr.co.junko.dto.OptionDTO;
import kr.co.junko.dto.UsingOptionDTO;
import kr.co.junko.util.Jwt;
import lombok.extern.slf4j.Slf4j;



@RestController
@CrossOrigin
@Slf4j
public class OptionController {

    @Autowired OptionService service;

    Map<String, Object> result = null;

    // 옵션 등록
    @PostMapping("option/insert")
    public Map<String, Object> optionInsert(
            @RequestBody OptionDTO dto,
            @RequestHeader Map<String, String> header) {
        
        log.info("dto : {}", dto);
        result = new HashMap<String, Object>();

        String token = header.get("authorization");
        Map<String, Object> payload = Jwt.readToken(token);
        String loginId = (String) payload.get("user_id");

        boolean login = loginId != null && !loginId.isEmpty();
        boolean success = false;

        if (login) {
            success = service.optionInsert(dto);
        }

        result.put("success", success);
        result.put("loginYN", login);
        return result;
    }

    // 옵션 수정
    @PutMapping("option/update")
    public Map<String, Object> optionUpdate(
        @RequestBody OptionDTO dto, 
        @RequestHeader Map<String, String> header) {
        log.info("dto : {}", dto);
        result = new HashMap<String,Object>();

        String token = header.get("authorization");
        Map<String, Object> payload = Jwt.readToken(token);
        String loginId = (String) payload.get("user_id");

        boolean login = loginId != null && !loginId.isEmpty();
        boolean success = false;

        if (login) {
            success = service.optionUpdate(dto);
        }

        result.put("success", success);
        result.put("loginYN", login);
        return result;
    }

    // 옵션 삭제
    @PutMapping("option/del/{option_idx}")
    public Map<String, Object> optionDel(
        @PathVariable("option_idx") int option_idx,
        @RequestHeader Map<String, String> header) {
        result = new HashMap<String,Object>();

        String token = header.get("authorization");
        Map<String, Object> payload = Jwt.readToken(token);
        String loginId = (String) payload.get("user_id");

        boolean login = loginId != null && !loginId.isEmpty();
        boolean success = false;

        if (login) {
            success = service.optionDel(option_idx);
        }

        result.put("success", success);
        result.put("loginYN", login);
        return result;
    }

    // 상품에 옵션 연결
    @PostMapping("/option/use")
    public Map<String, Object> optionUse(@RequestBody UsingOptionDTO dto) {

        result = new HashMap<String, Object>();

        boolean success = service.optionUse(dto);
        result.put("success", success);
        return result;
    }

    // 상품-옵션 연결 해제
    @PutMapping("/option/use/del/{using_idx}")
    public Map<String, Object> optionUseDel(
        @PathVariable("using_idx") int using_idx,
        @RequestHeader Map<String, String> header) {
        result = new HashMap<String, Object>();

        String token = header.get("authorization");
        Map<String, Object> payload = Jwt.readToken(token);
        String loginId = (String) payload.get("user_id");

        boolean login = loginId != null && !loginId.isEmpty();
        boolean success = false;

        if (login) {
            success = service.optionUseDel(using_idx);
        }

        result.put("success", success);
        result.put("loginYN", login);

        return result;
    }

    // 상품에 연결된 옵션 목록 조회
    @GetMapping("/option/using/{product_idx}")
    public Map<String, Object> usingOptionList(@PathVariable("product_idx") int product_idx) {
        result = new HashMap<String, Object>();
        List<UsingOptionDTO> list = service.usingOptionList(product_idx);
        result.put("list", list);
        return result;
    }

    // 옵션 조합 등록
    @PostMapping("/option/combined")
    public Map<String, Object> combinedInsert(@RequestBody CombinedDTO dto) {
        result = new HashMap<String, Object>();
        boolean success = service.combinedInsert(dto);
        result.put("success", success);
        return result;
    }

    // 조합 자동 생성
    // product_idx에 해당하는 상품의 옵션 조합을 자동으로 생성
    @PostMapping("/option/combined/auto/{product_idx}")
    public Map<String, Object> autoCombined(@PathVariable int product_idx) {
        boolean success = service.autoCombined(product_idx);
        result = new HashMap<String, Object>();
        result.put("success", success);
        result.put("message", success ? "조합 자동 생성 완료" : "조합 생성 실패");
        return result;
    }

    // 상품에 조합된 옵션 삭제
    @PutMapping("/option/product/del/{product_option_idx}")
    public Map<String, Object> productOptionDel(
        @PathVariable("product_option_idx") int idx,
        @RequestHeader Map<String, String> header) {
        result = new HashMap<String, Object>();

        String token = header.get("authorization");
        Map<String, Object> payload = Jwt.readToken(token);
        String loginId = (String) payload.get("user_id");

        boolean login = loginId != null && !loginId.isEmpty();
        boolean success = false;

        if (login) {
            success = service.productOptionDel(idx);
        }

        result.put("success", success);
        result.put("loginYN", login);

        return result;
    }

    // 조합된 옵션 삭제
    // combined_idx에 해당하는 조합된 옵션을 삭제
    @PutMapping("/option/combined/del/{combined_idx}")
    public Map<String, Object> combinedOptionsDel(
        @PathVariable("combined_idx") int combined_idx,
        @RequestHeader Map<String, String> header) {
        result = new HashMap<String, Object>();

        String token = header.get("authorization");
        Map<String, Object> payload = Jwt.readToken(token);
        String loginId = (String) payload.get("user_id");

        boolean login = loginId != null && !loginId.isEmpty();
        boolean success = false;

        if (login) {
            success = service.combinedOptionsDel(combined_idx);
        }

        result.put("success", success);
        result.put("loginYN", login);

        return result;
    }

    // 전체 조합 리스트 확인
    @GetMapping("/option/combined/list")
    public Map<String, Object> combinedList() {
        result = new HashMap<>();
        List<CombinedDTO> list = service.combinedList();
        result.put("list", list);
        return result;
    }

    // 특정 상품에 연결된 조합된 옵션 리스트 확인
    @GetMapping("/option/combined/list/{product_idx}")
    public Map<String, Object> combinedListProduct(@PathVariable int product_idx) {
        result = new HashMap<>();
        List<CombinedDTO> list = service.combinedListProduct(product_idx);
        result.put("list", list);
        return result;
    }

}