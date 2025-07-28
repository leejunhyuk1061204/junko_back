package kr.co.junko.stock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.MsgDTO;
import kr.co.junko.dto.ReceiveDTO;
import kr.co.junko.dto.ReturnHandleDTO;
import kr.co.junko.dto.SalesDTO;
import kr.co.junko.dto.ShipmentDTO;
import kr.co.junko.dto.StockDTO;
import kr.co.junko.dto.WarehouseDTO;
import kr.co.junko.dto.WaybillDTO;
import kr.co.junko.member.MemberDAO;
import kr.co.junko.msg.MsgDAO;
import kr.co.junko.product.ProductDAO;
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
	private final ProductDAO productDAO;
	private final MsgDAO msgDAO;
	private final MemberDAO memberDAO;

	@Transactional
	public boolean stockInsert(ReceiveDTO dto) {
		if(dto.getStockInfo() == null) throw new RuntimeException("입력받은 재고 정보가 없음");
		StockDTO stockDTO = null;
		
		// 1. 재고 정보 불러오기
		for(Map<String, Object>stockInfo : dto.getStockInfo()) {
			// stock_idx, product_idx, product_option_idx, manufacture,	expiration, stock_cnt, warehouse_idx, zone_idx, type, user_idx,	del_yn
			int product_option_idx = 0;
			stockDTO = new StockDTO();
			stockDTO.setProduct_idx((int)stockInfo.get("product_idx"));
			if(stockInfo.containsKey("product_option_idx") && stockInfo.get("product_option_idx") != null)  {
				stockDTO.setProduct_option_idx((int)stockInfo.get("product_option_idx"));
				product_option_idx = (int)stockInfo.get("product_option_idx");
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
			
			// 5. 쪽지 발송
			boolean msgSendResult = msgSend((int)stockInfo.get("product_idx"),product_option_idx);
		}
		// 4. 입고 상태 변경
		boolean receiveResult = receiveDAO.receiveUpdate(dto)>0;
		if(!receiveResult) throw new RuntimeException("입고 상태 변경 실패");
		
		// 6. 발주 상태 변경
		
		return true;
	}
	
	

	@Transactional
	public boolean stockInsert(ShipmentDTO dto) {
		// stock_idx, product_idx, product_option_idx, manufacture,	expiration, stock_cnt, warehouse_idx, zone_idx, type, user_idx,	del_yn
		// 1. 재고 등록
		StockDTO stock= null ;
		for (Map<String, Object> stockInfo : dto.getStockInfo()) {
			stock = new StockDTO();
			int product_option_idx= 0;
			stock.setProduct_idx((int)stockInfo.get("product_idx"));
			if(stockInfo.get("product_option_idx") != null) {
				stock.setProduct_option_idx((int)stockInfo.get("product_option_idx"));
				product_option_idx = (int)stockInfo.get("product_option_idx");
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
			
			// 5. 쪽지 발송
			msgSend((int)stockInfo.get("product_idx"),product_option_idx);
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

	@Transactional
	public boolean stockInsert(ReturnHandleDTO dto) {
		
		// 1. 재고 등록
		// stock_idx, product_idx, product_option_idx, manufacture,	expiration, stock_cnt, warehouse_idx, zone_idx, type, user_idx,	del_yn
		StockDTO stock = new StockDTO();
		int product_option_idx = 0;
		stock.setProduct_idx(dto.getProduct_idx());
		if(dto.getProduct_option_idx() != 0) {
			stock.setProduct_option_idx(dto.getProduct_option_idx());
			product_option_idx = dto.getProduct_option_idx();
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
		
		// 쪽지 발송
		msgSend(dto.getProduct_idx(),product_option_idx);
		
		return true;
	}

	public Map<String, Object> stockList(Map<String, Object> param) {
		Map<String, Object>result = new HashMap<String, Object>();
		if(param.get("page") != null) {
			int cnt = 10;
			int offset = ((int)param.get("page")-1)*cnt;
			param.put("cnt", cnt);
			param.put("offset", offset);
			int total = dao.StockListTotalPage(param);
			result.put("total", total);
		}
		List<Map<String, Object>>list = dao.StockList(param);
		result.put("list", list);
		return result;
	}

	public Map<String, Object> StockSumList(Map<String, Object> param) {
		Map<String, Object>result = new HashMap<String, Object>();
		if(param.get("page") != null) {
			int cnt = 10;
			int offset = ((int)param.get("page")-1)*cnt;
			param.put("cnt", cnt);
			param.put("offset", offset);
			int total = dao.StockSumListTotalPage(param);
			result.put("total", total);
		}
		List<Map<String, Object>>list = dao.StockSumList(param);
		result.put("list", list);
		return result;
	}

	public boolean stockUpdate(StockDTO dto) {
		return dao.stockUpdate(dto)>0;
	}

	@Transactional
	public boolean stockInsert(StockDTO dto) {
		boolean stockResult = dao.stockInsert(dto)>0;
		if(!stockResult) throw new RuntimeException("재고 조정 실패");
		int product_option_idx = 0;
		if(dto.getProduct_option_idx() != 0) {
			product_option_idx = dto.getProduct_option_idx();
		}
		msgSend(dto.getProduct_idx(),product_option_idx);
		
		return true;
	}

	public boolean stockDel(int idx) {
		return dao.stockDel(idx)>0;
	}

	public boolean msgSend(int product_idx,int product_option_idx) {
		// 5. 쪽지 발송
		// 5-1 stockSum 불러오기
		Map<String, Object> param = new HashMap<String, Object>();
		List<String>group = new ArrayList<String>();
		Map<String, Object>result = new HashMap<String, Object>();
		param.put("product_idx", product_idx);
		if(product_option_idx != 0)  {
			param.put("product_option_idx", product_option_idx);
			group.add("option");
		}
		List<Map<String, Object>>list = (List<Map<String, Object>>) StockSumList(param).get("list");
		// sum으로 해서 그런가 신기한 타입 
		// BigDecimal 은 정교한 계산에 사용되는 타입
		// int 변환 : intValue()
		// long 변환 : longValue()
		// double 변환 : doubleValue()
		log.info("type : {}", list.get(0).get("stock_sum").getClass().getName());
		BigDecimal stockSumBD = (BigDecimal) list.get(0).get("stock_sum"); 
		int stock_sum = stockSumBD.intValue();
		// 5-2 상품 min_cnt 가져오기
		int min_cnt = productDAO.selectProductIdx(product_idx).getMin_cnt();
		log.info("stock_sum : "+stock_sum);
		log.info("min_cnt : "+min_cnt);
		if(stock_sum <= min_cnt) {
			MsgDTO msgDTO = new MsgDTO();
			msgDTO.setMsg_title("재고 부족 알림");
			msgDTO.setMsg_content("상품코드 "+product_idx+" 번 상품의 재고가 부족합니다.");
			msgDTO.setImportant_yn(1);
			// 5-3 userList 가져오기
			param= new HashMap<String, Object>();
			param.put("job_idx", 1);
			param.put("dept_idx", 4);
			List<MemberDTO> userList = memberDAO.userList(param);
			for (MemberDTO user : userList) {
				msgDTO.setReceiver_idx(user.getUser_idx());
				log.info("msgDTO : {}",msgDTO);
				boolean success = msgDAO.msgInsert(msgDTO)>0;
				if(!success) throw new RuntimeException("쪽지 발송 실패");
			}
		}
		return true;
	}
	
}
