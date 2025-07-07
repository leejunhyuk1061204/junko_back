package kr.co.junko.taxInvoiceDetail;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TaxInvoiceDetailController {

	@Autowired
	private final TaxInvoiceDetailService service = null;
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
	
	
	// 품목 수정
	
	
	// 품목 삭
	
}
