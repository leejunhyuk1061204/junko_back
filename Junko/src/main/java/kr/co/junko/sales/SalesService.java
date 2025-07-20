package kr.co.junko.sales;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.FullSalesDTO;
import kr.co.junko.dto.SalesDTO;
import kr.co.junko.dto.SalesProductDTO;
import kr.co.junko.waybill.WaybillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalesService {

	private final SalesDAO dao;

	@Transactional
	public boolean salesInsert(FullSalesDTO dto) {
		
		boolean salesResult = dao.salesInsert(dto.getSales())>0;
		if(!salesResult) throw new RuntimeException("주문 등록 실패");
		
		for(SalesProductDTO product : dto.getProducts()) {
			product.setProduct_price(dao.searchPrice(product.getProduct_idx())*product.getProduct_cnt()); ;
			product.setSales_idx(dto.getSales().getSales_idx());
			boolean productResult = dao.salesProductInsert(product)>0;
			if(!productResult) throw new RuntimeException("상품 등록 실패");
		} 
		
		return true; 
	}

	public boolean salesUpdate(SalesDTO dto) {
		if("결제 완료".equals(dto.getStatus())) {
			dto.setPayment_date(LocalDate.now());
		}
			return dao.salesUpdate(dto)>0;
	}

	public Map<String, Object> salesList(Map<String, Object> param) {
		Map<String, Object>result = new HashMap<String, Object>();
		if(param.get("page") != null) {
			int cnt = 10;
			int offset = ((int)param.get("page")-1)*cnt;
			param.put("cnt", cnt);
			param.put("offset", offset);
			int total = dao.salesListTotalPage(param);
			result.put("total", total);
		}
		List<Map<String, Object>>list = dao.salesList(param);
		result.put("list", list);
		return result;
	}

	public boolean salesDel(int idx) {
		return dao.salesDel(idx)>0;
	}

	// 엑셀로 csv 파일 등록하니까 자동 "" 붙는 문제 발생
	// 메모장에 내용 복사 후 UTF-8 .csv 파일로 저장 후 업로드하면 정상 작동
	// 엑셀 다른이름 저장 -> 파일 형식을 CSV UTF-8 (Comma delimited) (*.csv)로 선택
	@Transactional
	public boolean salesCsvInsert(MultipartFile file) {
		
		// csv 파일 양식
		// 	1			2		3			4		5			6			7		8			9		10
		// 주문번호	고객	연락처		주소	결제방법	결제일		상태	상품코드	수량	가격
		
		if(file.isEmpty()) {return false;}
		try {
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
			
			String line;
			int successCount = 0;
			int cur_salesIdx = 0;
			String lastOrder_no = "";
			SalesDTO dto = null;
			
			line = reader.readLine();
			
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				
				for (int i = 0; i < values.length; i++) {
					values[i]=values[i].replace("\"", "").trim();
				}
				
				// 주문 정보 추출
				String order_no = values[0];
	            String customer = values[1];
	            String phone = values[2];
	            String address = values[3];
	            String paymentOption = values[4];
	            String paymentDateStr = values[5];
	            String status = values[6];

	            // 상품 정보 추출
	            int product_idx = Integer.parseInt(values[7]);
	            int product_option_idx= 0;
	            if(!values[8].isEmpty() && !values[8].equals("")) {
	            	product_option_idx = Integer.parseInt(values[8]);
	            }
	            int product_cnt = Integer.parseInt(values[9]);
	            int product_price = Integer.parseInt(values[10]);
				
	            
	            
	            // 주문번호가 같지 않으면 주문 등록
	            if(!order_no.equals(lastOrder_no)||lastOrder_no.equals("")) {
	            	dto = new SalesDTO();
					dto.setCustomer(customer.trim());
					dto.setCustomer_phone(phone.trim());
					dto.setCustomer_address(address.trim());
					dto.setPayment_option(paymentOption.trim());
					if(!paymentDateStr.isEmpty() && !paymentDateStr.equals("")) {
						dto.setPayment_date(LocalDate.parse(paymentDateStr.trim()));
					}
					dto.setStatus(status);
					int row = dao.salesInsert(dto);
					if(row>0) {
						successCount++;
						cur_salesIdx = dto.getSales_idx();
						lastOrder_no = order_no;
						
					}
				
				}
	            // 주문번호가 같지 않으면 이전 sales_idx
	            SalesProductDTO productDTO = new SalesProductDTO();
	            productDTO.setSales_idx(cur_salesIdx);
	            productDTO.setProduct_idx(product_idx);
	            if(product_option_idx != 0) {
	            	productDTO.setProduct_option_idx(product_option_idx);
	            }
	            productDTO.setProduct_cnt(product_cnt);
	            productDTO.setProduct_price(product_price);
	            dao.salesProductInsert(productDTO);
	            
			}
			
			log.info("CSV 상품 등록 성공 수 : {}", successCount);
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("CSV 파일 처리 중 오류 발생", e);
			throw new RuntimeException("CSV 파일 처리 실패",e);
		}
	}

	public SalesDTO salesDetailByIdx(int idx) {
		return dao.salesDetailByIdx(idx);
	}

	public boolean salesProductUpdate(SalesProductDTO dto) {
		return dao.salesProductUpdate(dto)>0;
	}

	public Map<String, Object> salesProductList(Map<String, Object> param) {
		Map<String, Object>result = new HashMap<String, Object>();
		if(param.get("page") != null) {
			int cnt = 10;
			int offset = ((int)param.get("page")-1)*cnt;
			param.put("cnt", cnt);
			param.put("offset", offset);
			int total = dao.salesProdcutListTotalPage(param);
			result.put("total", total);
		}
		List<Map<String, Object>>list = dao.salesProductList(param);
		result.put("list", list);
		return result;
	}

	public boolean salesProductDel(int sales_product_idx) {
		return dao.salesProductDel(sales_product_idx)>0;
	}
	
}
