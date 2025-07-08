package kr.co.junko.stock;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.ReceiveDTO;
import kr.co.junko.dto.ShipmentDTO;
import kr.co.junko.dto.StockDTO;
import kr.co.junko.receive.ReceiveDAO;
import kr.co.junko.shipment.ShipmentDAO;
import kr.co.junko.shipment.ShipmentService;
import kr.co.junko.warehouse.WarehouseService;
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
		if(!shipResult) throw new RuntimeException("출고 상태변경 실패");
		
		// 3. 주문 상태 변경
		
		return true;
	}

	
	
	public StockDTO stockDetailByIdx(int idx) {
		return dao.stockDetailByIdx(idx);
	}
	
}
