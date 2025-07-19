package kr.co.junko.invoiceTax;

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

import kr.co.junko.dto.InvoiceTaxDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class InvoiceTaxController {
    
    @Autowired InvoiceTaxService service;

    Map<String, Object> result = null;      

    
    // 세금계산서 등록
    @PostMapping("/invoice/insert")
    public Map<String, Object> insertInvoice(@RequestBody InvoiceTaxDTO dto) {
        result = new HashMap<String, Object>();

        boolean success = service.insertInvoice(dto);

        result.put("success", success);
        return result;
    }

    // 세금계산서 수정
    @PutMapping("/invoice/update")
    public Map<String, Object> updateInvoice(@RequestBody InvoiceTaxDTO dto) {
        result = new HashMap<String, Object>();

        boolean success = service.updateInvoice(dto);

        result.put("success", success);
        return result;
    }

    // 세금계산서 삭제
    @PutMapping("/invoice/del/{invoice_idx}")
    public Map<String, Object> delInvoice(@PathVariable int invoice_idx) {
        result = new HashMap<String, Object>();

        boolean success = service.delInvoice(invoice_idx);

        result.put("success", success);
        return result;
    }

    // 세금계산서 리스트
    @GetMapping("/invoice/list")
    public Map<String, Object> invoiceList(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate
    ) {
        result = new HashMap<String, Object>();
        int offset = (page - 1) * size;
        List<InvoiceTaxDTO> list = service.invoiceList(offset, size, status, keyword, startDate, endDate);
        int total = service.invoiceTotal(status, keyword, startDate, endDate);

        result.put("success", true);
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    // 세금계산서 상세
    @GetMapping("/invoice/detail/{invoice_idx}")
    public Map<String, Object> invoiceDetail(@PathVariable int invoice_idx) {
        result = new HashMap<String, Object>();

        InvoiceTaxDTO dto = service.invoiceDetail(invoice_idx);

        result.put("success", dto != null);
        result.put("data", dto);
        return result;
    }

    // 세금계산서 상태 변경
    @PutMapping("/invoice/status/{invoice_idx}")
    public Map<String, Object> updateInvoiceStatus(
        @PathVariable int invoice_idx, 
        @RequestBody Map<String, String> param) {
        result = new HashMap<String, Object>();
        String status = param.get("status");
        
        boolean success = service.updateInvoiceStatus(invoice_idx, status);

        result.put("success", success);
        return result;
    }


}
