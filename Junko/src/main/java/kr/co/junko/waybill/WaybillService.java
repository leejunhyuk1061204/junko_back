package kr.co.junko.waybill;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.claim.ClaimDAO;
import kr.co.junko.dto.ClaimDTO;
import kr.co.junko.dto.FullSalesDTO;
import kr.co.junko.dto.ReturnProductDTO;
import kr.co.junko.dto.ReturnReceiveDTO;
import kr.co.junko.dto.ReturnWaybillDTO;
import kr.co.junko.dto.SalesDTO;
import kr.co.junko.dto.SalesProductDTO;
import kr.co.junko.dto.WarehouseDTO;
import kr.co.junko.dto.WaybillDTO;
import kr.co.junko.returnReceive.ReturnReceiveDAO;
import kr.co.junko.sales.SalesDAO;
import kr.co.junko.sales.SalesService;
import kr.co.junko.shipment.ShipmentService;
import kr.co.junko.warehouse.WarehouseDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WaybillService {

	private final WaybillDAO dao;
	private final SalesDAO salesDAO;
	private final ClaimDAO claimDAO;
	private final WarehouseDAO warehouseDAO;
	private final ReturnReceiveDAO returnReceiveDAO;
	private final ShipmentService shipmentService;
	private final SalesService salesService;
	
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
		ReturnWaybillDTO returnWaybillDTO = new ReturnWaybillDTO();
		returnWaybillDTO.setClaim_idx(dto.getClaim_idx());
		returnWaybillDTO.setPickup_com_date(LocalDate.now().plusDays(1));
		returnWaybillDTO.setCustom_idx(dto.getCustom_idx());
		
		log.info("returnWaybillDTO : {}",returnWaybillDTO);
		boolean waybillResult = dao.returnWaybillInsert(returnWaybillDTO)>0;
		if(!waybillResult) throw new RuntimeException("송장 등록 실패");
		
		// 2. 반품 등록
		// claim_idx, return_waybill_idx, warehouse_idx, user_idx
		ReturnReceiveDTO returnReceiveDTO = new ReturnReceiveDTO();
		returnReceiveDTO.setClaim_idx(dto.getClaim_idx());
		returnReceiveDTO.setReturn_waybill_idx(returnWaybillDTO.getReturn_waybill_idx());
		WarehouseDTO warehouse =  warehouseDAO.getWarehouseByIdx(dto.getWarehouse_idx());
		returnReceiveDTO.setWarehouse_idx(dto.getWarehouse_idx());
		returnReceiveDTO.setUser_idx(warehouse.getUser_idx());
		returnReceiveDTO.setReceive_date(LocalDate.now().plusDays(1));
		
		boolean receiveResult = returnReceiveDAO.returnReceiveInsert(returnReceiveDTO)>0;
		if(!receiveResult) throw new RuntimeException("반품 등록 실패");
	
		
		// 3. 교환 시 주문 등록
		ClaimDTO claimDTO = claimDAO.claimDetailByIdx(dto.getClaim_idx());
		if("교환".equals(claimDTO.getType())) {
			FullSalesDTO fullSalesDTO = new FullSalesDTO();
			
			// 주문
			// customer, customer_phone, customer_address, payment_option, payment_date, status
			SalesDTO salesDTO = salesDAO.salesDetailByIdx(claimDTO.getSales_idx());
			salesDTO.setPayment_date(LocalDate.now());
			salesDTO.setStatus("결제 완료");
			fullSalesDTO.setSales(salesDTO);
			
			// 상품
			// product_idx, product_cnt, product_option_idx
			List<ReturnProductDTO>returnProduct = claimDAO.returnProductByClaimIdx(dto.getClaim_idx());
			List<SalesProductDTO>productList = new ArrayList<SalesProductDTO>();
			for(ReturnProductDTO p : returnProduct) {
				SalesProductDTO salesProduct = new SalesProductDTO();
				salesProduct.setProduct_idx(p.getProduct_idx());
				salesProduct.setProduct_cnt(p.getReturn_cnt());
				if(p.getProduct_option_idx() != 0) {
					if(p.getExchange_option() != 0) {
						salesProduct.setProduct_option_idx(p.getExchange_option());
					}else {
						salesProduct.setProduct_option_idx(p.getProduct_option_idx());
					}
				}
				productList.add(salesProduct);
			}
			
			fullSalesDTO.setProducts(productList);
			salesService.salesInsert(fullSalesDTO);
		}
		// 4. 클레임 상태 변경
		boolean claimResult = claimDAO.claimUpdate(dto)>0;
		if(!claimResult) throw new RuntimeException("클레임 상태 변경 실패");
		
		return true;
	}

	public boolean returnWaybillUpdate(ReturnWaybillDTO dto) {
		return dao.returnWaybillUpdate(dto)>0;
	}

	public Map<String, Object> returnWaybillList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<ReturnWaybillDTO>list = dao.returnWaybillList(param);
		int total = dao.returnWaybillListTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public boolean returnWaybillDel(int idx) {
		return dao.returnWaybillDel(idx)>0;
	}
	
}
