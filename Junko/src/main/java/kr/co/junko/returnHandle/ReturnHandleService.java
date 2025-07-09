package kr.co.junko.returnHandle;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.ReturnHandleDTO;
import kr.co.junko.dto.ReturnReceiveDTO;
import kr.co.junko.returnReceive.ReturnReceiveDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReturnHandleService {

	private final ReturnHandleDAO dao;
	private final ReturnReceiveDAO returnReceiveDAO;

	@Transactional
	public boolean returnHandleInsert(ReturnReceiveDTO dto) {
		
		// 1. 반품 처리 입력
		// return_receive_idx, disposal_reson, user_idx, product_idx,product_option_idx, disposal_cnt, resell_cnt
		List<ReturnHandleDTO>list = dto.getHandle();
		for(ReturnHandleDTO h : list) {
			h.setReturn_receive_idx(dto.getReturn_receive_idx());
		}
		
		// 2. 반품 입고 상태 수정
		boolean updateResult = returnReceiveDAO.returnReceiveUpdate(dto)>0;
		if(!updateResult) throw new RuntimeException("반품입고 수정 실패");
		
		return true;
	}
	
}
