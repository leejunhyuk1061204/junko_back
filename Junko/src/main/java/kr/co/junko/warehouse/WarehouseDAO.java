package kr.co.junko.warehouse;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.WarehouseDTO;

@Mapper
public interface WarehouseDAO {

	int warehouseInsert(WarehouseDTO dto);

}
