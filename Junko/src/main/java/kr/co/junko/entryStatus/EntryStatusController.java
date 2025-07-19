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

import kr.co.junko.dto.EntryStatusDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class EntryStatusController {
    
    @Autowired EntryStatusService service;

    Map<String, Object> result = null;

    // 정산 등록
    @PostMapping("/settlement/insert")
    public Map<String, Object> settlementInsert(@RequestBody EntryStatusDTO dto) {
        log.info("dto: {}", dto);
        result = new HashMap<String, Object>();

        boolean success = service.settlementInsert(dto);

        result.put("success", success);
        return result;
    }

    // 정산 리스트
    @GetMapping("/settlement/list")
    public Map<String, Object> settlementList(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String keyword
    ) {
        result = new HashMap<String, Object>();
        int offset = (page - 1) * size;

        List<EntryStatusDTO> list = service.settlementList(status, keyword, offset, size);
        int total = service.settlementTotal(status, keyword);

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


}
