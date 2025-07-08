package kr.co.junko.warehouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.WarehouseDTO;
import kr.co.junko.dto.ZoneDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WarehouseService {

	private final WarehouseDAO dao;

	public boolean warehouseInsert(WarehouseDTO dto) {
		int row = dao.warehouseInsert(dto);
		return row>0;
	}

	public boolean warehouseUpdate(WarehouseDTO dto) {
		int row = dao.warehouseUpdate(dto);
		return row>0;
	}

	public Map<String, Object> warehouseList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<WarehouseDTO>list = dao.warehouseList(param);
		int total = dao.warehouseTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public boolean warehouseDel(int idx) {
		int row = dao.warehouseDel(idx);
		return row>0;
	}

	public boolean zoneInsert(ZoneDTO dto) {
		return dao.zoneInsert(dto)>0;
	}

	public boolean zoneUpdate(ZoneDTO dto) {
		return dao.zoneUpdate(dto)>0;
	}

	public Map<String, Object> zoneList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<ZoneDTO>list = dao.zoneList(param);
		int total = dao.zoneListTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public boolean zoneDel(int idx) {
		return dao.zoneDel(idx)>0;
	}

	public int getWarehouseByZoneIdx(int idx) {
		return dao.getWarehouseByZoneIdx(idx);
	}
	
	
	
}
