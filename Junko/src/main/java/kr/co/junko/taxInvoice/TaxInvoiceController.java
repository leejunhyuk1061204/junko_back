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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.TaxInvoiceDTO;
import kr.co.junko.util.Jwt;
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
	
	
	@GetMapping("/taxInvoiceList/{page}")
	public Map<String, Object> taxInvoiceList(@PathVariable String page) {
	    result = new HashMap<>();
	    result = service.taxInvoiceList(page);
	    return result;
	}

	@GetMapping("/taxInvoiceSearch")
	public Map<String, Object> taxInvoiceSearch(@RequestParam int page,
	                                            @RequestParam(required = false) String status,
	                                            @RequestParam(required = false) String search,
	                                            @RequestParam(required = false) String sort) {
	    return service.taxInvoiceSearch(page, status, search, sort);
	}

	@GetMapping("/taxInvoice/{invoice_idx}")
	public Map<String, Object> taxInvoice(@PathVariable int invoice_idx) {
	    return service.taxInvoice(invoice_idx);
	}

	// 등록 (JWT)
	@PostMapping("/taxInvoiceAdd")
	public Map<String, Object> taxInvoiceAdd(@RequestBody TaxInvoiceDTO dto,
	                                         @RequestHeader Map<String, String> header) {
	    String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
	    result = new HashMap<>();

	    if (loginId != null && !loginId.isEmpty()) {
	        int user_idx = service.userIdxByLoginId(loginId);
	        dto.setUser_idx(user_idx);
	        result = service.taxInvoiceAdd(dto);
	        result.put("loginYN", true);
	    } else {
	        result.put("success", false);
	        result.put("message", "로그인 정보가 없습니다.");
	        result.put("loginYN", false);
	    }
	    return result;
	}

	// 수정 (JWT)
	@PutMapping("/taxInvoiceUpdate/{invoice_idx}")
	public Map<String, Object> taxInvoiceUpdate(@PathVariable int invoice_idx,
	                                            @RequestBody TaxInvoiceDTO dto,
	                                            @RequestHeader Map<String, String> header) {
	    String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
	    result = new HashMap<>();

	    if (loginId != null && !loginId.isEmpty()) {
	        int user_idx = service.userIdxByLoginId(loginId);
	        dto.setUser_idx(user_idx);
	        dto.setInvoice_idx(invoice_idx);
	        result = service.taxInvoiceUpdate(dto);
	        result.put("loginYN", true);
	    } else {
	        result.put("success", false);
	        result.put("message", "로그인 정보가 없습니다.");
	        result.put("loginYN", false);
	    }
	    return result;
	}

	// 삭제 (JWT)
	@DeleteMapping("/taxInvoiceDel/{invoice_idx}")
	public Map<String, Object> taxInvoiceDel(@PathVariable int invoice_idx,
	                                         @RequestHeader Map<String, String> header) {
	    String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");
	    result = new HashMap<>();

	    if (loginId != null && !loginId.isEmpty()) {
	        result = service.taxInvoiceDel(invoice_idx);
	        result.put("loginYN", true);
	    } else {
	        result.put("success", false);
	        result.put("message", "로그인 정보가 없습니다.");
	        result.put("loginYN", false);
	    }
	    return result;
	}

	// 상태 변경 (JWT)
	@PatchMapping("/taxStatusUpdate/{invoice_idx}/status")
	public Map<String, Object> taxStatusUpdate(@PathVariable int invoice_idx,
	                                           @RequestBody Map<String, String> req,
	                                           @RequestHeader Map<String, String> header) {
	    result = new HashMap<>();
	    String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

	    if (loginId != null && !loginId.isEmpty()) {
	        int user_idx = service.userIdxByLoginId(loginId);
	        String newStatus = req.get("status");
	        String memo = req.get("memo");

	        result = service.taxStatusUpdate(invoice_idx, newStatus, String.valueOf(user_idx), memo);
	        result.put("loginYN", true);
	    } else {
	        result.put("success", false);
	        result.put("message", "로그인 정보 없음");
	        result.put("loginYN", false);
	    }
	    return result;
	}

	@GetMapping("/taxLogList/{invoice_idx}")
	public Map<String, Object> taxLogList(@PathVariable int invoice_idx) {
	    return service.taxLogList(invoice_idx);
	}
}