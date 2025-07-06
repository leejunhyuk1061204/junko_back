package kr.co.junko.stock;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.ReceiveDTO;
import kr.co.junko.receive.ReceiveDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

	private final StockDAO dao;
	private final ReceiveDAO receiveDAO;

	public boolean stockInsert(ReceiveDTO dto) {
		
		// 1. 재고 정보 불러오기
		// product_idx
		//product_option_idx
		//stock_cnt
		//manufacture
		//expiration
		//warehouse_idx
		//zone_idx
		//type
		//user_idx
		
		// 2. 재고 등록
		// 3. 입고 상태 변경
		
		return false;
	}
	
}
