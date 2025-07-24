package kr.co.junko.invoiceTax;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.InvoiceDetailDTO;
import kr.co.junko.dto.InvoiceTaxDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InvoiceTaxService {

    @Autowired InvoiceTaxDAO dao;

    public boolean insertInvoice(InvoiceTaxDTO dto) {
        int total = 0;
        for (InvoiceDetailDTO detail : dto.getDetails()) {
            // 각 항목의 총합 = 수량 * 가격
            detail.setTotal_amount(detail.getQuantity() * detail.getPrice());
            // 그 총합들 누적합
            total += detail.getTotal_amount();
        }
        dto.setTotal_amount(total);

        boolean inserted = dao.insertInvoice(dto);
        if (!inserted) return false;

        for (InvoiceDetailDTO detail : dto.getDetails()) {
            detail.setInvoice_idx(dto.getInvoice_idx());
            dao.insertInvoiceDetail(detail);
        }
        return true;
    }

    public boolean updateInvoice(InvoiceTaxDTO dto) {
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

}
