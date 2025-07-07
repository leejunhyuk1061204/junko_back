package kr.co.junko.sales;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.SalesDTO;

@Mapper
public interface SalesDAO {

	int salesInsert(SalesDTO dto);

	int salesUpdate(SalesDTO dto);

	List<SalesDTO> salesList(Map<String, Object> param);

	int salesListTotalPage(Map<String, Object> param);

	int salesDel(int idx);

	SalesDTO salesDetailByIdx(int idx);

}
