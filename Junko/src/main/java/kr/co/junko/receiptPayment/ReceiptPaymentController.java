package kr.co.junko.receiptPayment;

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
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.ReceiptPaymentDTO;
import lombok.extern.slf4j.Slf4j;


@RestController
@CrossOrigin
@Slf4j
public class ReceiptPaymentController {

    @Autowired ReceiptPaymentService service;

    Map<String, Object> result = null;
    
    // 수금 등록
    @PostMapping("/receipt/insert")
    public Map<String, Object> receiptInsert(@RequestBody ReceiptPaymentDTO dto) {
        log.info("dto : {}", dto);
        result = new HashMap<String, Object>();

        dto.setType("수금");
        boolean success = service.receiptInsert(dto);

        result.put("success", success);
        return result;
    }
    
    // 지급 등록
    @PostMapping("/payment/insert")
    public Map<String, Object> paymentInsert(@RequestBody ReceiptPaymentDTO dto) {
        log.info("dto : {}", dto);
        result = new HashMap<String, Object>();

        dto.setType("지급");
        boolean success = service.paymentInsert(dto);

        result.put("success", success);
        return result;
    }

    // 수정 (수금/지급 공통)
    @PutMapping("/receipt/update")
    public Map<String, Object> receiptUpdate(@RequestBody ReceiptPaymentDTO dto) {
        log.info("dto : {}", dto);
        result = new HashMap<String, Object>();

        boolean success = service.receiptUpdate(dto);

        result.put("success", success);
        return result;
    }

    // 삭제 (수금/지급 공통)
    @PutMapping("receipt/del/{rp_idx}")
    public Map<String, Object> receiptDel(@PathVariable int rp_idx) {
        result = new HashMap<String, Object>();

        boolean success = service.receiptDel(rp_idx);

        result.put("success", success);
        return result;
    }

    // 수금 리스트
    @GetMapping("/receipt/list")
    public Map<String, Object> receiptList() {
        result = new HashMap<String, Object>();

        List<ReceiptPaymentDTO> list = service.receiptList("수금");

        result.put("success", true);
        result.put("list", list);
        return result;
    }

    // 지급 리스트
    @GetMapping("/payment/list")
    public Map<String, Object> paymentList() {
        result = new HashMap<String, Object>();

        List<ReceiptPaymentDTO> list = service.paymentList("지급");

        result.put("success", true);
        result.put("list", list);
        return result;
    }

    // 수금 상세
    @GetMapping("/receipt/detail/{rp_idx}")
    public Map<String, Object> detailReceipt(@PathVariable int rp_idx) {
        result = new HashMap<String, Object>();

        ReceiptPaymentDTO dto = service.detailReceipt(rp_idx);

        result.put("success", dto != null && "수금".equals(dto.getType()));
        result.put("data", dto);
        return result;
    }

    // 지급 상세
    @GetMapping("/payment/detail/{rp_idx}")
    public Map<String, Object> detailPayment(@PathVariable int rp_idx) {
        result = new HashMap<String, Object>();
        
        ReceiptPaymentDTO dto = service.detailPayment(rp_idx);

        result.put("success", dto != null && "지급".equals(dto.getType()));
        result.put("data", dto);
        return result;
    }
}