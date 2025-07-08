package kr.co.junko.waybill;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.SalesDTO;
import kr.co.junko.dto.WaybillDTO;
import kr.co.junko.sales.SalesDAO;
import kr.co.junko.shipment.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WaybillService {

	private final WaybillDAO dao;
	private final SalesDAO salesDAO;
	private final ShipmentService shipmentService;

	@Transactional
	public boolean waybillInsert(SalesDTO dto) {
		
		// 1. 송장 등록
		boolean waybillResult = dao.waybillInsert(dto)>0;
		if(!waybillResult) throw new RuntimeException("송장 등록 실패");
		
		// 2. 출고 등록
		boolean shipResult = shipmentService.shipmentInsert(dto.getSales_idx(),dto.getWaybill_idx());
		if(!shipResult) throw new RuntimeException("출고 등록 실패");
		
		// 3. 주문 상태 변경
		dto.setPayment_date(LocalDate.now());
		boolean salesResult = salesDAO.salesUpdate(dto)>0;
		if(!salesResult) throw new RuntimeException("주문 상태 변경 실패");
		
		
		return true;
	}

	public boolean waybillUpdate(WaybillDTO dto) {
		return dao.waybillUpdate(dto)>0;
	}

	public Map<String, Object> waybillList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<WaybillDTO>list = dao.waybillList(param);
		int total = dao.waybillListTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public boolean waybillDel(int idx) {
		return dao.waybillDel(idx)>0;
	}
	
}
