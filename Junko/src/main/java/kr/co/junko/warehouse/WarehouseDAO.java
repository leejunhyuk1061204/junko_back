package kr.co.junko.warehouse;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.WarehouseDTO;
import kr.co.junko.dto.ZoneDTO;

@Mapper
public interface WarehouseDAO {

	int warehouseInsert(WarehouseDTO dto);

	int warehouseUpdate(WarehouseDTO dto);

	List<WarehouseDTO> warehouseList(Map<String, Object> param);

	int warehouseTotalPage(Map<String, Object> param);

	int warehouseDel(int idx);

	int zoneInsert(ZoneDTO dto);

	int zoneUpdate(ZoneDTO dto);

	List<ZoneDTO> zoneList(Map<String, Object> param);

	int zoneListTotalPage(Map<String, Object> param);

	int zoneDel(int idx);

}
