package kr.co.junko.stock;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.StockDTO;

@Mapper
public interface StockDAO {

	int stockInsert(StockDTO stockDTO);

	StockDTO stockDetailByIdx(int idx);


}
