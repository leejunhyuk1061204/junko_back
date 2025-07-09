package kr.co.junko.waybill;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.claim.ClaimDAO;
import kr.co.junko.claim.ClaimService;
import kr.co.junko.dto.ClaimDTO;
import kr.co.junko.dto.ReturnWaybillDTO;
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
	private final ClaimDAO claimDAO;

	@Transactional
	public boolean waybillInsert(WaybillDTO dto) {
		
		// 1. 송장 등록
		boolean waybillResult = dao.waybillInsert(dto)>0;
		if(!waybillResult) throw new RuntimeException("송장 등록 실패");
		
		// 2. 출고 등록
		boolean shipResult = shipmentService.shipmentInsert(dto);
		if(!shipResult) throw new RuntimeException("출고 등록 실패");
		
		// 3. 주문 상태 변경
		SalesDTO salesDTO = new SalesDTO();
		salesDTO.setSales_idx(dto.getSales_idx());
		salesDTO.setStatus("출고 예정");
		boolean salesResult = salesDAO.salesUpdate(salesDTO)>0;
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
	
	
	@Transactional
	public boolean returnWaybillInsert(ClaimDTO dto) {

		// claim_handle insert 시 status 에 '처리완료' 일 경우 송장 자동생성
		// 아직 테스트 안함
		
		// 1. return 송장 등록
		// claim_idx, pickup_com_date, custom_idx
		ReturnWaybillDTO returnDTO = new ReturnWaybillDTO();
		returnDTO.setClaim_idx(dto.getClaim_idx());
		returnDTO.setPickup_com_date(LocalDate.now().plusDays(1));
		returnDTO.setCustom_idx(dto.getCustom_idx());
		
		boolean returnResult = dao.returnWaybillInsert(returnDTO)>0;
		
		// 2. 교환 시 주문 등록
		// 어떤 상품 교환하는지 product_idx, product_option_idx 가 테이블에 있어야하지 않을까 ?
		// 있다면 어느 테이블에 있을까 ?
		// 1:N이면 목록 새로 만들어야하는데 ..
		
		
		
		
		// 3. 클레임 상태 변경
		boolean claimResult = claimDAO.claimUpdate(dto)>0;
		if(!claimResult) throw new RuntimeException("클레임 상태 변경 실패");
		
		return true;
	}
	
}
