package kr.co.junko.invoiceTax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.EntryDetailDTO;
import kr.co.junko.dto.InvoiceDetailDTO;
import kr.co.junko.dto.InvoiceTaxDTO;
import kr.co.junko.dto.ReceiptPaymentDTO;
import kr.co.junko.dto.VoucherDTO;
import kr.co.junko.receiptPayment.ReceiptPaymentService;
import kr.co.junko.voucher.VoucherDAO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InvoiceTaxService {

    @Autowired InvoiceTaxDAO dao;
    @Autowired VoucherDAO voucherDAO;
    @Autowired ReceiptPaymentService receiptPaymentService;

    public boolean insertInvoice(InvoiceTaxDTO dto) {
        int total = 0;
        for (InvoiceDetailDTO detail : dto.getDetails()) {
            // 각 항목의 총합 = 수량 * 가격
            detail.setTotal_amount(detail.getQuantity() * detail.getPrice());
            // 그 총합들 누적합
            total += detail.getTotal_amount();
        }
        dto.setTotal_amount(total);

        // 결재자 리스트 null 방어 처리
        if (dto.getApprover_ids() == null) {
            dto.setApprover_ids(new ArrayList<>());
        }

        boolean inserted = dao.insertInvoice(dto);
        if (!inserted) return false;

        for (InvoiceDetailDTO detail : dto.getDetails()) {
            detail.setInvoice_idx(dto.getInvoice_idx());
            dao.insertInvoiceDetail(detail);
        }

        if (dto.getEntry_idx() != null && dto.getEntry_type() != null && dto.getTotal_amount() > 0) {
            ReceiptPaymentDTO rpDto = new ReceiptPaymentDTO();
            rpDto.setEntry_idx(dto.getEntry_idx());
            rpDto.setCustom_idx(dto.getCustom_idx());
            rpDto.setAmount(dto.getTotal_amount());
            rpDto.setNote("세금계산서 자동 생성");

            if ("매출".equals(dto.getEntry_type())) {
                receiptPaymentService.receiptInsert(rpDto); // 수금
            } else if ("매입".equals(dto.getEntry_type())) {
                receiptPaymentService.paymentInsert(rpDto); // 지급
            }
        }


        return true;
    }

    public boolean updateInvoice(InvoiceTaxDTO dto) {
        if (dto.getApprover_ids() == null) {
            dto.setApprover_ids(new ArrayList<>());
        }

        if (dto.getDetails() == null) {
            dto.setDetails(new ArrayList<>());
        }

        int updated = dao.updateInvoice(dto);
        if (updated <= 0) return false;

        // 기존 디테일 삭제 후 다시 등록
        dao.delInvoiceDetail(dto.getInvoice_idx());
        for (InvoiceDetailDTO detail : dto.getDetails()) {
            detail.setInvoice_idx(dto.getInvoice_idx());
            dao.insertInvoiceDetail(detail);
        }

        return true;
    }

    public boolean delInvoice(int invoice_idx) {
        int row = dao.delInvoice(invoice_idx);
        dao.delInvoiceDetail(invoice_idx);
        return row > 0;
    }

    public List<InvoiceTaxDTO> invoiceList(int offset, int size, String status, String keyword, String startDate, String endDate, String sort, String order) {
        return dao.invoiceList(offset, size, status, keyword, startDate, endDate, sort, order);
    }

    public int invoiceTotal(String status, String keyword, String startDate, String endDate, String sort, String order) {
        return dao.invoiceTotal(status, keyword, startDate, endDate, sort, order);
    }

    public InvoiceTaxDTO invoiceDetail(int invoice_idx) {
        InvoiceTaxDTO dto = dao.invoiceDetail(invoice_idx);
        if (dto != null) {
            dto.setDetails(dao.invoiceDetailList(invoice_idx));
        }
        return dto;
    }

    public boolean updateInvoiceStatus(int invoice_idx, String status) {
        int row = dao.updateInvoiceStatus(invoice_idx, status);
        return row > 0;
    }

    public boolean existsInvoiceEntryIdx(int entry_idx) {
        return dao.existsInvoiceEntryIdx(entry_idx);
    }

    // 세금계산서 품목 자동 생성
    public boolean insertInvoiceWithDetails(VoucherDTO voucher, List<EntryDetailDTO> details) {

        // 1. 헤더 먼저 세팅
        InvoiceTaxDTO dto = new InvoiceTaxDTO();
        dto.setEntry_idx(voucher.getEntry_idx());
        dto.setCustom_idx(voucher.getCustom_idx());
        dto.setTotal_amount(voucher.getAmount());
        dto.setIssued_by("자동생성");
        dto.setStatus("작성중");
        dto.setApprover_ids(new ArrayList<>()); 

        if (dto.getApprover_ids() == null) {
            dto.setApprover_ids(new ArrayList<>());
        }

        boolean inserted = dao.insertInvoice(dto);
        if (!inserted) return false;

        Set<Integer> processedAmounts = new HashSet<>();
        for (EntryDetailDTO d : details) {
            if (d.getDel_yn() == 1 || !"차변".equals(d.getType())) continue;

            // 동일 금액이면 하나만 처리 (중복 방지)
            if (processedAmounts.contains(d.getAmount())) continue;

            InvoiceDetailDTO item = new InvoiceDetailDTO();
            item.setInvoice_idx(dto.getInvoice_idx());
            item.setItem_name(voucherDAO.selectAsNameByIdx(d.getAs_idx()));
            item.setQuantity(1);
            item.setPrice(d.getAmount());
            item.setTotal_amount(d.getAmount());

            dao.insertInvoiceDetail(item);
            processedAmounts.add(d.getAmount());
        }

        return true;
    }

    public boolean insertAutoInvoice(int entry_idx) {
        // 이미 세금계산서 존재하면 중단
        if (dao.existsInvoiceEntryIdx(entry_idx)) return false;

        // 전표 정보 조회
        VoucherDTO voucher = voucherDAO.voucherDetail(entry_idx);
        if (voucher == null) return false;

        // 분개 항목 조회
        List<EntryDetailDTO> details = voucherDAO.entryDetailList(entry_idx);
        if (details == null || details.isEmpty()) return false;

        // 세금계산서 생성
        return insertInvoiceWithDetails(voucher, details);
    }
}
