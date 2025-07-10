package kr.co.junko.stock;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.ReceiveDTO;
import kr.co.junko.dto.ReturnHandleDTO;
import kr.co.junko.dto.SalesDTO;
import kr.co.junko.dto.ShipmentDTO;
import kr.co.junko.dto.StockDTO;
import kr.co.junko.dto.WarehouseDTO;
import kr.co.junko.dto.WaybillDTO;
import kr.co.junko.receive.ReceiveDAO;
import kr.co.junko.returnReceive.ReturnReceiveDAO;
import kr.co.junko.sales.SalesDAO;
import kr.co.junko.shipment.ShipmentDAO;
import kr.co.junko.shipment.ShipmentService;
import kr.co.junko.warehouse.WarehouseService;
import kr.co.junko.waybill.WaybillDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

	private final StockDAO dao;
	private final ReceiveDAO receiveDAO;
	private final WarehouseService warehouseService;
	private final ShipmentDAO shipmentDAO;
	private final SalesDAO salesDAO;
	private final WaybillDAO waybillDAO;
	private final ReturnReceiveDAO returnReceiveDAO;

	@Transactional
	public boolean stockInsert(ReceiveDTO dto) {
		if(dto.getStockInfo() == null) throw new RuntimeException("입력받은 재고 정보가 없음");
		StockDTO stockDTO = null;
		// 1. 재고 정보 불러오기
		for(Map<String, Object>stockInfo : dto.getStockInfo()) {
			// stock_idx, product_idx, product_option_idx, manufacture,	expiration, stock_cnt, warehouse_idx, zone_idx, type, user_idx,	del_yn
			stockDTO = new StockDTO();
			stockDTO.setProduct_idx((int)stockInfo.get("product_idx"));
			if(stockInfo.containsKey("product_option_idx")) {
				stockDTO.setProduct_option_idx((int)stockInfo.get("product_option_idx"));
			}
			if(stockInfo.containsKey("manufacture")) {
				stockDTO.setManufacture(LocalDate.parse((String)stockInfo.get("manufacture")));
			}
			if(stockInfo.containsKey("expiration")) {
				stockDTO.setExpiration(LocalDate.parse((String)stockInfo.get("expiration")));
			}
			stockDTO.setZone_idx((int)stockInfo.get("zone_idx"));
			int warehouse_idx = warehouseService.getWarehouseByZoneIdx((int)stockInfo.get("zone_idx"));
			stockDTO.setWarehouse_idx(warehouse_idx);
			stockDTO.setStock_cnt((int)stockInfo.get("stock_cnt"));
			stockDTO.setUser_idx(dto.getUser_idx());
			stockDTO.setType("입고");
			// 3. 재고 등록
			boolean stockResult = dao.stockInsert(stockDTO)>0; 
			if(!stockResult) throw new RuntimeException("재고 등록 실패");
		}
		// 4. 입고 상태 변경
		boolean receiveResult = receiveDAO.receiveUpdate(dto)>0;
		if(!receiveResult) throw new RuntimeException("입고 상태 변경 실패");
		
		return true;
	}

	@Transactional
	public boolean stockInsert(ShipmentDTO dto) {
		// stock_idx, product_idx, product_option_idx, manufacture,	expiration, stock_cnt, warehouse_idx, zone_idx, type, user_idx,	del_yn
		// 1. 재고 등록
		StockDTO stock= null ;
		for (Map<String, Object> stockInfo : dto.getStockInfo()) {
			stock = new StockDTO();
			stock.setProduct_idx((int)stockInfo.get("product_idx"));
			if(stockInfo.get("product_option_idx") != null) {
				stock.setProduct_option_idx((int)stockInfo.get("product_option_idx"));
			}
			if(stockInfo.get("manufacture") != null) {
				stock.setManufacture(LocalDate.parse((String)stockInfo.get("manufacture")));
			}
			if(stockInfo.get("expiration") != null) {
				stock.setExpiration(LocalDate.parse((String)stockInfo.get("expiration"))); 
			}
			stock.setWarehouse_idx(dto.getWarehouse_idx());
			stock.setZone_idx((int)stockInfo.get("zone_idx"));
			stock.setStock_cnt(-(int)stockInfo.get("stock_cnt"));
			stock.setUser_idx(dto.getUser_idx());
			stock.setType("출고");
			
			boolean stockResult = dao.stockInsert(stock)>0;
			if(!stockResult) throw new RuntimeException("재고 등록 실패");
		}
		
		// 2. 출고 상태변경
		boolean shipResult = shipmentDAO.shipmentUpdate(dto)>0;
		if(!shipResult) throw new RuntimeException("출고 상태 변경 실패");
		
		ShipmentDTO shipmentDTO = shipmentDAO.shipmentDetailByIdx(dto.getShipment_idx());
		
		// 3. 주문 상태 변경
		SalesDTO salesDTO = new SalesDTO();
		salesDTO.setSales_idx(shipmentDTO.getSales_idx());
		salesDTO.setStatus("배송중");
		boolean salesResult = salesDAO.salesUpdate(salesDTO)>0;
		if(!salesResult) throw new RuntimeException("주문 상태 변경 실패");
		
		// 4. 송장 상태 변경
		WaybillDTO waybillDTO = new WaybillDTO();
		waybillDTO.setWaybill_idx(shipmentDTO.getWaybill_idx());
		waybillDTO.setStatus("배송중");
		boolean waybillResult = waybillDAO.waybillUpdate(waybillDTO)>0;
		if(!waybillResult) throw new RuntimeException("송장 상태 변경 실패");
		
		return true;
	}

	
	
	public StockDTO stockDetailByIdx(int idx) {
		return dao.stockDetailByIdx(idx);
	}

	public boolean stockInsert(ReturnHandleDTO dto) {
		
		// 1. 재고 등록
		// stock_idx, product_idx, product_option_idx, manufacture,	expiration, stock_cnt, warehouse_idx, zone_idx, type, user_idx,	del_yn
		StockDTO stock = new StockDTO();
		stock.setProduct_idx(dto.getProduct_idx());
		if(dto.getProduct_option_idx() != 0) {
			stock.setProduct_option_idx(dto.getProduct_option_idx());
		}
		stock.setStock_cnt(dto.getResell_cnt());
		stock.setUser_idx(dto.getUser_idx());
		stock.setType("반품");
		
		if(dto.getManufacture() != null) {
			stock.setManufacture(dto.getManufacture());
		}
		if(dto.getExpiration() != null) {
			stock.setExpiration(dto.getExpiration());
		}
		int warehouse_idx = warehouseService.getWarehouseByZoneIdx(dto.getZone_idx());
		stock.setWarehouse_idx(warehouse_idx);
		stock.setZone_idx(dto.getZone_idx());
		
		boolean stockResult = dao.stockInsert(stock)>0;
		if(!stockResult) throw new RuntimeException("재고 등록 실패");
		
		return true;
	}

	public Map<String, Object> stockList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<StockDTO>list = dao.StockList(param);
		int total = dao.StockListTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public Map<String, Object> StockSumList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<Map<String, Object>>list = dao.StockSumList(param);
		int total = dao.StockSumListTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public boolean stockUpdate(StockDTO dto) {
		return dao.stockUpdate(dto)>0;
	}

	public boolean stockInsert(StockDTO dto) {
		return dao.stockInsert(dto)>0;
	}

	public boolean stockDel(int idx) {
		return dao.stockDel(idx)>0;
	}
	
}
