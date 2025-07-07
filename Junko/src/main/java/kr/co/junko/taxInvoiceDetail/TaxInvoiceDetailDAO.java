package kr.co.junko.taxInvoiceDetail;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.TaxInvoiceDetailDTO;

@Mapper
public interface TaxInvoiceDetailDAO {

	List<TaxInvoiceDetailDTO> taxProductList(int invoice_idx);

	TaxInvoiceDetailDTO taxProductOne(int invoice_idx, int detail_idx);

}
