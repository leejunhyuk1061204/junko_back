package kr.co.junko.taxInvoice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.TaxInvoiceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
@CrossOrigin
public class TaxInvoiceController {

	@Autowired
	private final TaxInvoiceService service;
	Map<String, Object> result = null;
	
	
	// 세금계산서 리스트 조회 (페이징)
	@GetMapping(value="/taxInvoiceList/{page}")
	public Map<String, Object> taxInvoiceList(@PathVariable String page) {
	    result = new HashMap<String, Object>();
	    result = service.taxInvoiceList(page);
	    return result;
	}
	
	// 검색
	@GetMapping("/taxInvoiceSearch")
	public Map<String, Object> taxInvoiceSearch(
	    @RequestParam int page,
	    @RequestParam(required = false) String status,
	    @RequestParam(required = false) String search,
	    @RequestParam(required = false) String sort
	) {
	    return service.taxInvoiceSearch(page, status, search, sort);
	}

	
	// 단건 조회
	@GetMapping(value="/taxInvoice/{invoice_idx}")
	public Map<String, Object> taxInvoice(@PathVariable int invoice_idx) {
	    return service.taxInvoice(invoice_idx);
	}

	// 세금계산서 등록
	@PostMapping(value="/taxInvoiceAdd")
	public Map<String, Object> taxInvoiceAdd(@RequestBody TaxInvoiceDTO dto) {
	    return service.taxInvoiceAdd(dto);
	}

	
	// 세금계산서 수정
	@PutMapping(value="/taxInvoiceUpdate/{invoice_idx}")
	public Map<String, Object> taxInvoiceUpdate(@PathVariable int invoice_idx, @RequestBody TaxInvoiceDTO dto) {
	    dto.setInvoice_idx(invoice_idx);
	    return service.taxInvoiceUpdate(dto);
	}

	
	// 세금계산서 삭제 
	@DeleteMapping(value="/taxInvoiceDel/{invoice_idx}")
	public Map<String, Object> taxInvoiceDel(@PathVariable int invoice_idx) {
	    return service.taxInvoiceDel(invoice_idx);
	}

	// 상태 변경 & 로그 저장
	@PatchMapping(value="/taxStatusUpdate/{invoice_idx}/status")
	public Map<String, Object>taxStatusUpdate(@PathVariable int invoice_idx ,@RequestBody Map<String, String> req){
		result = new HashMap<String, Object>();
		
		String newStatus = req.get("status");
		String status_by= req.get("status_by");
		String memo = req.get("memo");
		result = service.taxStatusUpdate(invoice_idx,newStatus,status_by,memo);
		
		return result;
	}
	
	
	// 로그 조회
	@GetMapping(value="/taxLogList/{invoice_idx}")
	public Map<String, Object> taxLogList(@PathVariable int invoice_idx) {
	    return service.taxLogList(invoice_idx);
	}
	
	
}
