package kr.co.junko.taxInvoice;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.junko.dto.TaxInvoiceDTO;
import kr.co.junko.dto.TaxInvoiceLogDTO;

@Mapper
public interface TaxInvoiceDAO {

	List<TaxInvoiceDTO> taxInvoiceList(int offset, int limit);

	int pages(int limit);

	TaxInvoiceDTO taxInvoice(int invoice_idx);

	int taxInvoiceAdd(TaxInvoiceDTO dto);

	int taxInvoicUpdate(TaxInvoiceDTO dto);

	int taxInvoiceDel(int invoice_idx);

	int taxStatusUpdate(int invoice_idx, String newStatus);

	void saveLog(TaxInvoiceLogDTO dto);

	List<TaxInvoiceLogDTO> taxLogList(@Param("invoice_idx") int invoice_idx);

}
