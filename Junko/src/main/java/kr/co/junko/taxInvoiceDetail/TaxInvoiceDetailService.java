package kr.co.junko.taxInvoiceDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.TaxInvoiceDetailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaxInvoiceDetailService {

	@Autowired
	private final TaxInvoiceDetailDAO dao;
	Map<String, Object> result = null;

	public Map<String, Object> taxProductList(int invoice_idx) {
		result = new HashMap<String, Object>();
		
		List<TaxInvoiceDetailDTO> dto = dao.taxProductList(invoice_idx);
		
		
		if (dto == null || dto.isEmpty()) {
	        result.put("success", false);
	        result.put("message", "해당 세금계산서에 품목이 없습니다.");
	    } else {
	        result.put("success", true);
	        result.put("dto", dto);
	    }
		
		return result;
	}

	public Map<String, Object> taxProductOne(int invoice_idx, int detail_idx) {
		result = new HashMap<String, Object>();
		
		TaxInvoiceDetailDTO dto = dao.taxProductList(invoice_idx, detail_idx);
		
		if (dto == null) {
	        result.put("success", false);
	        result.put("message", "해당 품목을 찾을 수 없습니다.");
	    } else {
	        result.put("success", true);
	        result.put("dto", dto);
	    }

	    return result;
	}
	
	
}
