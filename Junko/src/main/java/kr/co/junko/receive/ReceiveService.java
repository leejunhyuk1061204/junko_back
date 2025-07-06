package kr.co.junko.receive;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.OrderDTO;
import kr.co.junko.dto.ReceiveDTO;
import kr.co.junko.dto.ReceiveProductDTO;
import kr.co.junko.order.OrderDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReceiveService {

	private final ReceiveDAO dao;
	private final OrderDAO orderDAO;

	@Transactional
	public boolean fullInsertReceive(OrderDTO dto) {
		
		ReceiveDTO receive = null;
		ReceiveProductDTO product = null;
		
		// 1. 입고 정보 가져오기
		// order_idx,user_idx,receive_date
		// receive_idx,product_idx,product_option_idx,receive_cnt
		List<Map<String, Object>>info = dao.receiveInfo(dto.getOrder_idx());
		for (Map<String, Object> param : info) {
			receive = new ReceiveDTO();
			receive.setOrder_idx(dto.getOrder_idx());
			receive.setUser_idx((int)param.get("warehouse_idx"));
			Date sqlDate = (Date)param.get("delivery_date");
			LocalDate date = sqlDate.toLocalDate();
			receive.setReceive_date(date);
			
			boolean receiveResult = insertReceive(receive);	// 2. 입고 등록
			if(!receiveResult) throw new RuntimeException("입고 등록 실패");
			
			product = new ReceiveProductDTO();
			product.setReceive_idx(receive.getReceive_idx());
			product.setProduct_idx((int)param.get("product_idx"));
			product.setReceive_cnt((int)param.get("order_cnt"));
			if(param.get("product_option_idx") != null) {
				product.setProduct_option_idx((int)param.get("product_option_idx"));
			}
			
			boolean productResult = insertReceiveProduct(product);	// 3. 입고 상품 등록
			if(!productResult) throw new RuntimeException("입고상품 등록 실패");
		}
		
		// 4. order state 업데이트
			boolean updateState = orderDAO.orderUpdate(dto)>0;
			if(!updateState) throw new RuntimeException("발주 상태 변경 실패");
		
		return true;
	}
	
	public boolean insertReceive(ReceiveDTO dto) {
		return dao.insertReceive(dto)>0;
	}
	
	public boolean insertReceiveProduct(ReceiveProductDTO dto) {
		return dao.insertReceiveProduct(dto)>0;
	}

	public boolean receiveUpdate(ReceiveDTO dto) {
		return dao.receiveUpdate(dto)>0;
	}
	
	public boolean receiveProductUpdate(ReceiveProductDTO dto) {
		return dao.receiveProductUpdate(dto)>0;
	}

	public Map<String, Object> receiveList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<ReceiveDTO>list = dao.receiveList(param);
		int total = dao.receiveTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public Map<String, Object> receiveProductList(Map<String, Object> param) {
		int cnt = 5;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<ReceiveProductDTO>list = dao.receiveProductList(param);
		int total = dao.receiveProductTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}
	
	@Transactional
	public boolean receiveDel(int idx) {
		boolean receiveResult = dao.receiveDel(idx)>0;
		if(!receiveResult) throw new RuntimeException("입고 삭제 실패");
		
		boolean productResult = dao.receiveProductDel(idx)>0;
		if(!productResult) throw new RuntimeException("입고 상품 삭제 실패");
		
		return true;
	}

	

	
	
}
