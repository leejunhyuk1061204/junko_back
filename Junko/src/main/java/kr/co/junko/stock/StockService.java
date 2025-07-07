package kr.co.junko.stock;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.ReceiveDTO;
import kr.co.junko.dto.StockDTO;
import kr.co.junko.receive.ReceiveDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

	private final StockDAO dao;
	private final ReceiveDAO receiveDAO;

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
	
}
