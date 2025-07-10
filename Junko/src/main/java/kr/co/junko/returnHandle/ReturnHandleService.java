package kr.co.junko.returnHandle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.ReturnHandleDTO;
import kr.co.junko.dto.ReturnReceiveDTO;
import kr.co.junko.dto.WaybillDTO;
import kr.co.junko.returnReceive.ReturnReceiveDAO;
import kr.co.junko.stock.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReturnHandleService {

	private final ReturnHandleDAO dao;
	private final ReturnReceiveDAO returnReceiveDAO;
	private final StockService stockService;

	@Transactional
	public boolean returnHandleInsert(ReturnReceiveDTO dto) {
		
		// 1. 반품 처리 입력
		// return_receive_idx, disposal_reason, user_idx, product_idx,product_option_idx, disposal_cnt, resell_cnt
		List<ReturnHandleDTO>list = dto.getHandle();
		for(ReturnHandleDTO h : list) {
			h.setReturn_receive_idx(dto.getReturn_receive_idx());
			h.setUser_idx(dto.getUser_idx());
			
			boolean handleResult = dao.returnHandleInsert(h)>0;
			if(!handleResult) throw new RuntimeException("반품 처리 입력 실패");
			
			// 2. 재고 등록
			if(h.getResell_cnt()>0) {
				boolean stockResult = stockService.stockInsert(h);
				if(!handleResult) throw new RuntimeException("재고 등록 실패");
			}
		}
		
		// 3. 반품 입고 상태 수정
		boolean updateResult = returnReceiveDAO.returnReceiveUpdate(dto)>0;
		if(!updateResult) throw new RuntimeException("반품입고 수정 실패");
		
		return true;
	}

	public boolean returnHandleUpdate(ReturnHandleDTO dto) {
		return dao.returnHandleUpdate(dto)>0;
	}

	public Map<String, Object> returnHandleList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<ReturnHandleDTO>list = dao.returnHandleList(param);
		int total = dao.returnHandleListTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public boolean returnHandleDel(int idx) {
		return dao.returnHandleDel(idx)>0;
	}
	
}
