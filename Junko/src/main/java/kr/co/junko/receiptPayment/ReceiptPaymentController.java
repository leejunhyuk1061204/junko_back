package kr.co.junko.receiptPayment;

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
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.custom.CustomService;
import kr.co.junko.document.DocumentService;
import kr.co.junko.dto.ApprovalLineDTO;
import kr.co.junko.dto.CustomDTO;
import kr.co.junko.dto.DocumentCreateDTO;
import kr.co.junko.dto.DocumentDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.ReceiptPaymentDTO;
import kr.co.junko.file.FileDAO;
import lombok.extern.slf4j.Slf4j;


@RestController
@CrossOrigin
@Slf4j
public class ReceiptPaymentController {

    @Autowired ReceiptPaymentService service;
    @Autowired DocumentService documentService;
    @Autowired CustomService customService;
    @Autowired FileDAO filedao;

    Map<String, Object> result = null;
    
    // 수금 등록
    @PostMapping("/receipt/insert")
    public Map<String, Object> receiptInsert(@RequestBody ReceiptPaymentDTO dto) {
        log.info("dto : {}", dto);
        result = new HashMap<>();

        dto.setType("수금");

        CustomDTO custom = customService.customSelect(dto.getCustom_idx());
        if (custom != null) {
            dto.setCustomer_name(custom.getCustom_name());
        }

        boolean success = service.receiptInsert(dto);

        result.put("success", success);
        result.put("data", dto);

        if (success) {
            Map<String, String> variables = receiptPaymentToVariables(dto);
            result.put("variables", variables);

            DocumentCreateDTO docDto = new DocumentCreateDTO();
            docDto.setTemplate_idx(dto.getTemplate_idx());
            docDto.setUser_idx(dto.getUser_idx());
            docDto.setVariables(variables);
            docDto.setType("receipt_payment");
            docDto.setIdx(dto.getRp_idx());
            docDto.setApprover_ids(dto.getApprover_ids());

            documentService.documentInsert(docDto);
        }

        return result;
    }
    
    private Map<String, String> receiptPaymentToVariables(ReceiptPaymentDTO dto) {
        Map<String, String> map = new HashMap<>();

        map.put("cap_idx", String.valueOf(dto.getRp_idx()));
        map.put("type", dto.getType());
        map.put("date", dto.getTransaction_date() != null ? dto.getTransaction_date().toString() : "");
        map.put("amount", String.format("%,d", dto.getAmount()));
        map.put("customName", dto.getCustomer_name() != null ? dto.getCustomer_name() : "");
        map.put("memo", dto.getNote() != null ? dto.getNote() : "");
        map.put("entry_idx", dto.getEntry_idx() != null ? String.valueOf(dto.getEntry_idx()) : "");

        // 거래처 정보 조회
        CustomDTO custom = customService.customSelect(dto.getCustom_idx());
        if (custom != null) {
            map.put("accountBank", custom.getBank() != null ? custom.getBank() : "");
            map.put("accountNumber", custom.getAccount_number() != null ? custom.getAccount_number() : "");
        } else {
            map.put("accountBank", "");
            map.put("accountNumber", "");
        }

        return map;
    }

    
    // 지급 등록
    @PostMapping("/payment/insert")
    public Map<String, Object> paymentInsert(@RequestBody ReceiptPaymentDTO dto) {
        log.info("dto : {}", dto);
        result = new HashMap<>();

        dto.setType("지급");

        CustomDTO custom = customService.customSelect(dto.getCustom_idx());
        if (custom != null) {
            dto.setCustomer_name(custom.getCustom_name());
        }
        
        boolean success = service.paymentInsert(dto);

        result.put("success", success);
        result.put("data", dto);

        if (success) {
            Map<String, String> variables = receiptPaymentToVariables(dto);
            result.put("variables", variables);

            DocumentCreateDTO docDto = new DocumentCreateDTO();
            docDto.setTemplate_idx(dto.getTemplate_idx());
            docDto.setUser_idx(dto.getUser_idx());
            docDto.setVariables(variables);
            docDto.setType("receipt_payment");
            docDto.setIdx(dto.getRp_idx());
            docDto.setApprover_ids(dto.getApprover_ids());

            documentService.documentInsert(docDto);
        }

        return result;
    }

    // 수정 (수금/지급 공통)
    @PutMapping("/receipt/update")
    public Map<String, Object> receiptUpdate(@RequestBody ReceiptPaymentDTO dto) {
        log.info("dto : {}", dto);
        result = new HashMap<>();

        boolean success = service.receiptUpdate(dto);
        result.put("success", success);

        if (success) {
            Map<String, String> variables = receiptPaymentToVariables(dto);
            result.put("variables", variables);

        // 1. 기존 결재자 삭제
        documentService.delApprovalLines(dto.getDocument_idx());

        // 2. 새 결재자 삽입
        if (dto.getApprover_ids() != null) {
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

        // 3. 문서 업데이트
        DocumentCreateDTO docDto = new DocumentCreateDTO();
        docDto.setDocument_idx(dto.getDocument_idx());
        docDto.setTemplate_idx(dto.getTemplate_idx());
        docDto.setUser_idx(dto.getUser_idx());
        docDto.setVariables(variables);
        docDto.setType("receipt_payment");
        docDto.setIdx(dto.getRp_idx());
        docDto.setApprover_ids(dto.getApprover_ids());

        documentService.documentUpdate(docDto);
        }

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

    // 수금,지급 상세
    @GetMapping("/receiptPayment/detail/{rp_idx}")
    public Map<String, Object> detailReceiptPayment(@PathVariable int rp_idx) {
        result = new HashMap<>();

        ReceiptPaymentDTO dto = service.detailReceiptPayment(rp_idx);
        boolean success = dto != null;
        result.put("success", success);
        result.put("data", dto);

        if (success) {
            DocumentDTO doc = documentService.getByTypeAndIdx("receipt_payment", rp_idx);
            result.put("document", doc);

            if (doc != null) {
                dto.setDocument_idx(doc.getDocument_idx());

                List<ApprovalLineDTO> lines = documentService.getApprovalLines(doc.getDocument_idx());
                List<Integer> approverIds = new ArrayList<>();
                for (ApprovalLineDTO line : lines) {
                    approverIds.add(line.getUser_idx());
                }
                result.put("approver_ids", approverIds);
                result.put("approval_lines", lines);

                Map<String, Object> fileParam = new HashMap<>();
                fileParam.put("type", "document");
                fileParam.put("idx", doc.getDocument_idx());
                fileParam.put("ext", "pdf");
                FileDTO file = filedao.selectTypeIdx(fileParam);
                result.put("file", file);
            }

            Map<String, String> variables = receiptPaymentToVariables(dto);
            result.put("variables", variables);
        }

        result.put("success", true);
        result.put("data", dto);
        return result;
    }

}