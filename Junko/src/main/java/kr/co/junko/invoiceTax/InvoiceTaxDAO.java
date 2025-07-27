package kr.co.junko.invoiceTax;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.InvoiceDetailDTO;
import kr.co.junko.dto.InvoiceTaxDTO;

@Mapper
public interface InvoiceTaxDAO {

    boolean insertInvoice(InvoiceTaxDTO dto);

    void insertInvoiceDetail(InvoiceDetailDTO detail);

    int updateInvoice(InvoiceTaxDTO dto);

    void delInvoiceDetail(int invoice_idx);

    int delInvoice(int invoice_idx);

    List<InvoiceTaxDTO> invoiceList(int offset, int size, String status, String keyword, String startDate, String endDate, String sort, String order);

    int invoiceTotal(String status, String keyword, String startDate, String endDate, String sort, String order);

    InvoiceTaxDTO invoiceDetail(int invoice_idx);

    List<InvoiceDetailDTO> invoiceDetailList(int invoice_idx);

    int updateInvoiceStatus(int invoice_idx, String status);

    boolean existsInvoiceEntryIdx(int entry_idx);

}
