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

import kr.co.junko.document.DocumentService;
import kr.co.junko.dto.DocumentCreateDTO;
import kr.co.junko.dto.DocumentDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.InvoiceTaxDTO;
import kr.co.junko.file.FileDAO;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class InvoiceTaxController {
    
    @Autowired InvoiceTaxService service;
    @Autowired DocumentService documentService;
    @Autowired FileDAO filedao;

    Map<String, Object> result = null;      

    
    // 세금계산서 등록
    @PostMapping("/invoice/insert")
    public Map<String, Object> insertInvoice(@RequestBody InvoiceTaxDTO dto) {
        result = new HashMap<String, Object>();

        boolean success = service.insertInvoice(dto);
        result.put("success", success);
        
        if (success) {
            Map<String, String> variables = invoiceToVariables(dto);

            DocumentCreateDTO docDto = new DocumentCreateDTO();
            docDto.setTemplate_idx(13);
            docDto.setUser_idx(dto.getUser_idx());
            docDto.setVariables(variables);
            docDto.setType("invoice");
            docDto.setIdx(dto.getInvoice_idx());

            Map<String, Object> docResult = documentService.documentInsert(docDto);
            int document_idx = (int) docResult.get("document_idx");

            dto.setDocument_idx(document_idx);

            result.put("variables", variables);
            result.put("invoice_idx", dto.getInvoice_idx());
            result.put("document_idx", document_idx);
        }

        return result;
    }

    // 세금계산서 수정
    @PutMapping("/invoice/update")
    public Map<String, Object> updateInvoice(@RequestBody InvoiceTaxDTO dto) {
        result = new HashMap<String, Object>();

        boolean success = service.updateInvoice(dto);
        result.put("success", success);        

        if (success) {
            Map<String, String> variables = invoiceToVariables(dto);

            DocumentCreateDTO docDto = new DocumentCreateDTO();
            docDto.setTemplate_idx(13);
            docDto.setUser_idx(dto.getUser_idx());
            docDto.setVariables(variables);
            docDto.setType("invoice");
            docDto.setIdx(dto.getInvoice_idx());
            docDto.setDocument_idx(dto.getDocument_idx());

            documentService.documentUpdate(docDto);

            result.put("variables", variables);
        }

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
        @RequestParam(required = false) String endDate,
        @RequestParam(defaultValue = "i.reg_date") String sort,
        @RequestParam(defaultValue = "desc") String order
    ) {
        result = new HashMap<String, Object>();
        int offset = (page - 1) * size;
        List<InvoiceTaxDTO> list = service.invoiceList(offset, size, status, keyword, startDate, endDate, sort, order);
        int total = service.invoiceTotal(status, keyword, startDate, endDate, sort, order);

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
        
        if (dto == null) {
            result.put("success", false);
            return result;
        }
        
        // 문서 정보 연동
        DocumentDTO doc = documentService.getByTypeAndIdx("invoice", invoice_idx);
        if (doc != null) {
            dto.setDocument_idx(doc.getDocument_idx());

            Map<String, Object> fileParam = new HashMap<>();
            fileParam.put("type", "document");
            fileParam.put("idx", doc.getDocument_idx());
            fileParam.put("ext", "pdf");

            FileDTO file = filedao.selectTypeIdx(fileParam);
            if (file != null) {
                dto.setFile_name(file.getNew_filename());
            }
        }

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

    // 세금 계산서 환경변수 세팅
    private Map<String, String> invoiceToVariables(InvoiceTaxDTO dto) {
        Map<String, String> map = new HashMap<>();
        
        int vat = (int) Math.floor(dto.getTotal_amount() * 0.1);
        int totalWithVat = dto.getTotal_amount() + vat;

        map.put("invoice_idx", String.valueOf(dto.getInvoice_idx()));
        map.put("custom_name", dto.getCustom_name() != null ? dto.getCustom_name() : "");
        map.put("issued_by", dto.getIssued_by() != null ? dto.getIssued_by() : "");
        map.put("reg_date", dto.getReg_date() != null ? dto.getReg_date().toString() : "");
        map.put("total_amount", String.valueOf(dto.getTotal_amount()));
        map.put("vat_amount", String.valueOf(vat));
        map.put("total_with_vat", String.valueOf(totalWithVat));
        map.put("status", dto.getStatus() != null ? dto.getStatus() : "작성중");
        map.put("user_name", dto.getIssued_by() != null ? dto.getIssued_by() : "");

        // 품목 리스트 -> HTML 테이블 로우로 변환
        StringBuilder itemsHtml = new StringBuilder();
        if (dto.getDetails() != null) {
            for (var item : dto.getDetails()) {
                itemsHtml.append("<tr>")
                    .append("<td>").append(item.getItem_name()).append("</td>")
                    .append("<td>").append(item.getQuantity()).append("</td>")
                    .append("<td>").append(item.getPrice()).append("</td>")
                    .append("<td>").append(item.getTotal_amount()).append("</td>")
                    .append("</tr>");
            }
        }
        map.put("items", itemsHtml.toString());

        return map;
    }

    // 세금계산서 미리보기
    @PostMapping("/invoice/preview")
    public Map<String, Object> invoicePreview(@RequestBody InvoiceTaxDTO dto) {
        result = new HashMap<>();

        Map<String, String> variables = invoiceToVariables(dto);
        String html = documentService.documentPreview(13, variables); // 템플릿 번호 13번 사용

        result.put("success", html != null);
        result.put("preview", html);
        result.put("variables", variables);
        return result;
    }


}
