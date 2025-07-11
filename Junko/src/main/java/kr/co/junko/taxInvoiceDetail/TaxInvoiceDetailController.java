package kr.co.junko.taxInvoiceDetail;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.TaxInvoiceDetailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
public class TaxInvoiceDetailController {

	@Autowired
	private final TaxInvoiceDetailService service;
	Map<String, Object> result = null;
	
	// 해당 세금계산서의 품목 리스트 조회;
	@GetMapping(value="/taxProductList/{invoice_idx}/detail")
	public Map<String, Object> taxProductList(@PathVariable int invoice_idx){
		return service.taxProductList(invoice_idx);
	}
	
	// 품목 단건 조회
	@GetMapping(value="/taxProductOne/{invoice_idx}/detail/{detail_idx}")
	public Map<String, Object> taxProductOne(@PathVariable int invoice_idx,
			@PathVariable int detail_idx){
		return service.taxProductOne(invoice_idx,detail_idx);
	}
	
	// 품목 추가
	@PostMapping(value="/addProdDetail/{invoice_idx}/detail")
	public Map<String, Object> addProdDetail(@PathVariable int invoice_idx, 
			@RequestBody TaxInvoiceDetailDTO dto){
		
		 dto.setInvoice_idx(invoice_idx);
		
		return service.addProdDetail(dto);
	}
	
	// 품목 수정
	@PutMapping(value="/prodDetailUpdate/{invoice_idx}/detail/{detail_idx}")
	public Map<String, Object> prodDetailUpdate(@PathVariable int invoice_idx, 
			@RequestBody TaxInvoiceDetailDTO dto,
			@PathVariable int detail_idx){
		
		dto.setInvoice_idx(invoice_idx);
	    dto.setDetail_idx(detail_idx);
		
		return service.prodDetailUpdate(dto);
	}
	
	// 품목 삭제
	@DeleteMapping(value="/prodDetailDel/{invoice_idx}/detail/{detail_idx}")
	public Map<String, Object> prodDetailDel(@PathVariable int invoice_idx,
			@PathVariable int detail_idx){
		return service.prodDetailDel(invoice_idx,detail_idx);
	}
	
}
