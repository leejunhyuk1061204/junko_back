package kr.co.junko.taxInvoiceDetail;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.TaxInvoiceDetailDTO;
import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
@CrossOrigin
public class TaxInvoiceDetailController {

	@Autowired
	private final TaxInvoiceDetailService service;
	Map<String, Object> result = null;
	
	// 세금계산서의 품목 리스트 조회
		@GetMapping("/taxProductList/{invoice_idx}/detail")
		public Map<String, Object> taxProductList(@PathVariable int invoice_idx) {
			return service.taxProductList(invoice_idx);
		}

		// 품목 단건 조회
		@GetMapping("/taxProductOne/{invoice_idx}/detail/{detail_idx}")
		public Map<String, Object> taxProductOne(@PathVariable int invoice_idx,
		                                         @PathVariable int detail_idx) {
			return service.taxProductOne(invoice_idx, detail_idx);
		}

		// 품목 추가 (JWT 인증)
		@PostMapping("/addProdDetail/{invoice_idx}/detail")
		public Map<String, Object> addProdDetail(@PathVariable int invoice_idx,
		                                         @RequestBody TaxInvoiceDetailDTO dto,
		                                         @RequestHeader Map<String, String> header) {

			result = new HashMap<>();
			String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

			if (loginId != null && !loginId.isEmpty()) {
				int user_idx = service.userIdxByLoginId(loginId);
				dto.setInvoice_idx(invoice_idx);
				dto.setUser_idx(user_idx);

				result = service.addProdDetail(dto);
				result.put("loginYN", true);
			} else {
				result.put("success", false);
				result.put("message", "로그인 정보 없음");
				result.put("loginYN", false);
			}
			return result;
		}

		// 품목 수정 (JWT 인증)
		@PutMapping("/prodDetailUpdate/{invoice_idx}/detail/{detail_idx}")
		public Map<String, Object> prodDetailUpdate(@PathVariable int invoice_idx,
		                                            @PathVariable int detail_idx,
		                                            @RequestBody TaxInvoiceDetailDTO dto,
		                                            @RequestHeader Map<String, String> header) {

			result = new HashMap<>();
			String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

			if (loginId != null && !loginId.isEmpty()) {
				int user_idx = service.userIdxByLoginId(loginId);
				dto.setInvoice_idx(invoice_idx);
				dto.setDetail_idx(detail_idx);
				dto.setUser_idx(user_idx);

				result = service.prodDetailUpdate(dto);
				result.put("loginYN", true);
			} else {
				result.put("success", false);
				result.put("message", "로그인 정보 없음");
				result.put("loginYN", false);
			}
			return result;
		}

		// 품목 삭제 (JWT 인증)
		@DeleteMapping("/prodDetailDel/{invoice_idx}/detail/{detail_idx}")
		public Map<String, Object> prodDetailDel(@PathVariable int invoice_idx,
		                                         @PathVariable int detail_idx,
		                                         @RequestHeader Map<String, String> header) {

			result = new HashMap<>();
			String loginId = (String) Jwt.readToken(header.get("authorization")).get("user_id");

			if (loginId != null && !loginId.isEmpty()) {
				result = service.prodDetailDel(invoice_idx, detail_idx);
				result.put("loginYN", true);
			} else {
				result = new HashMap<>();
				result.put("success", false);
				result.put("message", "로그인 정보 없음");
				result.put("loginYN", false);
			}
			return result;
		}
	}