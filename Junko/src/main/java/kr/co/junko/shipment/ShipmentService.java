package kr.co.junko.shipment;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.ShipmentDTO;
import kr.co.junko.dto.WaybillDTO;
import kr.co.junko.stock.StockService;
import kr.co.junko.warehouse.WarehouseDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShipmentService {

	private final ShipmentDAO dao;
	private final WarehouseDAO warehouseDAO;
	private final StockService stockService;

	public boolean shipmentInsert(WaybillDTO dto) {
		// user_idx, sales_idx, waybill_idx, shipment_date, warehouse_idx
		ShipmentDTO shipDTO = new ShipmentDTO();
		shipDTO.setSales_idx(dto.getSales_idx());
		shipDTO.setWaybill_idx(dto.getWaybill_idx());
		shipDTO.setWarehouse_idx(dto.getWarehouse_idx());
		int user_idx = warehouseDAO.getWarehouseByIdx(dto.getWarehouse_idx()).getUser_idx();
		shipDTO.setUser_idx(user_idx);
		shipDTO.setShipment_date(LocalDate.now().plusDays(1));
		
		return dao.shipmentInsert(shipDTO)>0;
	}

	public boolean shipmentUpdate(ShipmentDTO dto) {
		if("출고완료".equals(dto.getStatus())) {
			return stockService.stockInsert(dto);
		}
		return dao.shipmentUpdate(dto)>0;
	}

	public Map<String, Object> shipmentList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<ShipmentDTO>list = dao.shipmentList(param);
		int total = dao.shipmentListTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("param"));
		return result;
	}

	public boolean shipmentDel(int idx) {
		return dao.shipmentDel(idx)>0;
	}
	
	
	
}
