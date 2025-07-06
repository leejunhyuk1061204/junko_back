package kr.co.junko.stock;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.LotDTO;
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
		
		// 1. 재고 정보 불러오기
		// product_idx
		// product_option_idx
		// stock_cnt
		
		// warehouse_idx
		// category
		
		// zone_idx
		// manufacture
		// expiration
		// user_idx
		// type
		List<Map<String, Object>>info = dao.stockInfo(dto.getReceive_idx());
		// 2. 상품 카테고리 확인
		// 2-1. 필요 시 로트 확인
		// 2-2. 로트 있으면 로트번호 가져오기
		// 2-3. 로트 없으면 로트생성 후 로트번호 가져오기
        // 3. 재고 등록
		// 4. 입고 상태 변경
		
		return true;
	}
	
}
