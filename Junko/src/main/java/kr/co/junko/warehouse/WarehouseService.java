package kr.co.junko.warehouse;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.WarehouseDTO;
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
	
}
