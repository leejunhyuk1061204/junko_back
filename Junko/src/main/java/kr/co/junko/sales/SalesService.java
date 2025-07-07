package kr.co.junko.sales;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.SalesDTO;
import kr.co.junko.waybill.WaybillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalesService {

	private final SalesDAO dao;
	private final WaybillService waybillService;

	public boolean salesInsert(SalesDTO dto) {
		return dao.salesInsert(dto)>0;
	}

	public boolean salesUpdate(SalesDTO dto) {
		if("결제 완료".equals(dto.getStatus())) {
			String curState = salesDetailByIdx(dto.getSales_idx()).getStatus();
			if("결제 대기".equals(curState)) {
				return waybillService.waybillInsert(dto); 
			}
			return dao.salesUpdate(dto)>0;
		} else {
			return dao.salesUpdate(dto)>0;
		}
	}

	public Map<String, Object> salesList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<SalesDTO>list = dao.salesList(param);
		int total = dao.salesListTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("total", total);
		result.put("list", list);
		result.put("page", param.get("page"));
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
		
		if(file.isEmpty()) {return false;}
		try {
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
			
			String line;
			int successCount = 0;
			
			line = reader.readLine();
			
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				
				for (int i = 0; i < values.length; i++) {
					values[i]=values[i].replace("\"", "").trim();
				}
				
				SalesDTO dto = new SalesDTO();
				dto.setCustomer(values[0].trim());
				dto.setCustomer_phone(values[1].trim());
				dto.setCustomer_address(values[2].trim());
				dto.setPayment_option(values[3].trim());
				if(!values[4].isEmpty() && !values[4].equals("")) {
					dto.setPayment_date(LocalDate.parse(values[4].trim()));
				}
				dto.setStatus(values[5]);
				
				int row = dao.salesInsert(dto);
				if(row>0) {
					successCount++;
				}
			}
			
			log.info("CSV 상품 등록 성공 수 : {}", successCount);
			return successCount > 0;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("CSV 파일 처리 중 오류 발생", e);
			return false;
		}
	}

	public SalesDTO salesDetailByIdx(int idx) {
		return dao.salesDetailByIdx(idx);
	}
	
}
