package kr.co.junko.returnReceive;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ReturnReceiveDTO;
import kr.co.junko.dto.WaybillDTO;

@Mapper
public interface ReturnReceiveDAO {

	int returnReceiveInsert(ReturnReceiveDTO returnReceiveDTO);

	int returnReceiveUpdate(ReturnReceiveDTO dto);

	List<ReturnReceiveDTO> returnReceiveList(Map<String, Object> param);

	int returnReceiveListTotalPage(Map<String, Object> param);

	int returnReceiveDel(int idx);

}
