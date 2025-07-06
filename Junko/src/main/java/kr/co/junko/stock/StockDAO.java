package kr.co.junko.stock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.LotDTO;

@Mapper
public interface StockDAO {

	List<Map<String, Object>> stockInfo(int receive_idx);

	LotDTO findLot(int product_idx, LocalDate manufacture, LocalDate expiration, int product_option_idx);

	LotDTO findLot(LotDTO lotDTO);

	LotDTO insertLot(LotDTO lotDTO);

}
