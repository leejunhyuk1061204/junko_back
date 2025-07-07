package kr.co.junko.taxInvoiceFile;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.junko.dto.FileDTO;

@Mapper
public interface TaxInvoiceFileDAO {

	void taxInvoiceFile(FileDTO dto);

	List<FileDTO> invoiceFileList(@Param("invoice_idx") int invoice_idx,@Param("type") String type);

	FileDTO invoiceFileDown(@Param("file_idx")int file_idx);

}
