package kr.co.junko.warehouse;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.WarehouseDTO;

@Mapper
public interface WarehouseDAO {

	int warehouseInsert(WarehouseDTO dto);

	int warehouseUpdate(WarehouseDTO dto);

	List<WarehouseDTO> warehouseList(Map<String, Object> param);

	int warehouseTotalPage(Map<String, Object> param);

	int warehouseDel(int idx);

}
