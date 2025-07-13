package kr.co.junko.taxInvoice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.TaxInvoiceDTO;
import kr.co.junko.dto.TaxInvoiceLogDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaxInvoiceService {

	@Autowired
	private final TaxInvoiceDAO dao;
	private int limit = 10 , page = 0;
	Map<String, Object> result = null;
	
	public Map<String, Object> taxInvoiceList(String page) {
		Map<String, Object> result = new HashMap<String, Object>();
		this.page=Integer.parseInt(page);
		result.put("page", this.page);
		int offset = (this.page-1) * limit;
		result.put("list", dao.taxInvoiceList(offset,limit));
		result.put("pages", dao.pages(limit));
		
		return result;
	}

	public Map<String, Object> taxInvoice(int invoice_idx) {
		result = new HashMap<String, Object>();
	    TaxInvoiceDTO dto = dao.taxInvoice(invoice_idx);

	    if (dto == null) {
	        result.put("success", false);
	        result.put("message", "세금계산서를 찾을 수 없습니다.");
	    } else {
	        result.put("success", true);
	        result.put("dto", dto);
	    }

	    return result;
	}

	public Map<String, Object> taxInvoiceAdd(TaxInvoiceDTO dto) {
		result = new HashMap<String, Object>();
	    int row = dao.taxInvoiceAdd(dto); // 

	    if (row > 0) {
	        result.put("success", true);
	        result.put("message", "세금계산서 발행");
	        result.put("invoice_idx", dto.getInvoice_idx());
	    } else {
	        result.put("success", false);
	        result.put("message", "세금계산서 발행실패");
	    }

	    return result;
	}

	public Map<String, Object> taxInvoiceUpdate(TaxInvoiceDTO dto) {
		result = new HashMap<String, Object>();
		
		int row = dao.taxInvoiceUpdate(dto);

	    if (row > 0) {
	        result.put("success", true);
	        result.put("message", "세금계산서 수정완");
	    } else {
	        result.put("success", false);
	        result.put("message", "세금계산서 수정실패");
	    }

	    return result;
	}

	public Map<String, Object> taxInvoiceDel(int invoice_idx) {
		result = new HashMap<String, Object>();
		
		int row = dao.taxInvoiceDel(invoice_idx);

		    if (row > 0) {
		        result.put("success", true);
		        result.put("message", "세금계산서 삭제");
		    } else {
		        result.put("success", false);
		        result.put("message", "삭제할 세금계산서를 찾을 수 없습니다.");
		    }

		    return result;
	}

	public Map<String, Object> taxStatusUpdate(int invoice_idx, String newStatus, String status_by, String memo) {
		result = new HashMap<String, Object>();
		
		int row = dao.taxStatusUpdate(invoice_idx, newStatus);
		
		
		if (row > 0) {
		TaxInvoiceLogDTO dto = new TaxInvoiceLogDTO();
		dto.setInvoice_idx(invoice_idx);
		dto.setStatus(status_by);
		dto.setStatus_by(status_by);
		dto.setMemo(memo);
		dto.setStatus_time(LocalDateTime.now());
		
		dao.saveLog(dto);
			
		result.put("success", true);
		result.put("msg", "상태가 ["+newStatus+"]로 변경되었습니다.");
		}else {
		result.put("success", false);
		result.put("msg", "상태 변경 실패. 세금계산서를 찾을 수 없습니다.");
			
		
		}
			
		return result;
	}

	public Map<String, Object> taxLogList(int invoice_idx) {
		List<TaxInvoiceLogDTO> dto = dao.taxLogList(invoice_idx);
		result = new HashMap<String, Object>();
		
		 if (dto == null) {
		        result.put("success", false);
		        result.put("message", "로그를 찾을 수 없습니다.");
		    } else {
		        result.put("success", true);
		        result.put("dto", dto);
		    }

		    return result;
		
	}
	
	
}
