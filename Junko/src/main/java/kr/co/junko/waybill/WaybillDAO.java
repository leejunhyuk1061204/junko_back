package kr.co.junko.waybill;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.SalesDTO;
import kr.co.junko.dto.WaybillDTO;

@Mapper
public interface WaybillDAO {

	int waybillInsert(WaybillDTO dto);

	int waybillUpdate(WaybillDTO dto);

	List<WaybillDTO> waybillList(Map<String, Object> param);

	int waybillListTotalPage(Map<String, Object> param);

	int waybillDel(int idx);

}
