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

import kr.co.junko.dto.VoucherDTO;
import lombok.extern.slf4j.Slf4j;


@RestController
@CrossOrigin
@Slf4j
public class VoucherController {
    
    @Autowired VoucherService service;

    Map<String, Object> result = null;

    // 전표 등록
    @PostMapping("/voucher/insert")
    public Map<String, Object> voucherInsert(@RequestBody VoucherDTO dto){
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
        @RequestParam(defaultValue = "entry_date") String sort,
        @RequestParam(defaultValue = "desc") String order
    ) {
        int offset = (page - 1) * size;

        List<VoucherDTO> list = service.voucherList(entry_type, status, keyword, sort, order, offset, size);
        int total = service.voucherTotal(entry_type, status, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return result;
    }


    
}
