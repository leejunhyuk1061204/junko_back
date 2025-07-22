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
		Map<String, Object>result = new HashMap<String, Object>();
		if(param.get("page") != null) {
			int cnt = 10;
			int offset = ((int)param.get("page")-1)*cnt;
			param.put("cnt", cnt);
			param.put("offset", offset);
			int total = dao.warehouseTotalPage(param);
			result.put("total", total);
		}
		List<Map<String, Object>>list = dao.warehouseList(param);
		result.put("list", list);
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
		Map<String, Object>result = new HashMap<String, Object>();
		if(param.get("page") != null) {
			int cnt = 10;
			int offset = ((int)param.get("page")-1)*cnt;
			param.put("cnt", cnt);
			param.put("offset", offset);
			int total = dao.zoneListTotalPage(param);
			result.put("total", total);
		}
		List<Map<String, Object>>list = dao.zoneList(param);
		result.put("list", list);
		return result;
	}

	public boolean zoneDel(int idx) {
		return dao.zoneDel(idx)>0;
	}

	public int getWarehouseByZoneIdx(int idx) {
		return dao.getWarehouseByZoneIdx(idx);
	}

	public WarehouseDTO getWarehouseByIdx(int idx) {
		return dao.getWarehouseByIdx(idx);
	}
	
	
	
}
