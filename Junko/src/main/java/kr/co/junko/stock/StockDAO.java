package kr.co.junko.stock;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.StockDTO;
import kr.co.junko.dto.WaybillDTO;

@Mapper
public interface StockDAO {

	int stockInsert(StockDTO stockDTO);

	StockDTO stockDetailByIdx(int idx);

	List<StockDTO> StockList(Map<String, Object> param);

	int StockListTotalPage(Map<String, Object> param);

	List<Map<String, Object>> StockSumList(Map<String, Object> param);

	int StockSumListTotalPage(Map<String, Object> param);

	int stockUpdate(StockDTO dto);

	int stockDel(int idx);
}
