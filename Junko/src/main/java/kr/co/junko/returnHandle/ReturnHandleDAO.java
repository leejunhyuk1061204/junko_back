package kr.co.junko.returnHandle;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ReturnHandleDTO;
import kr.co.junko.dto.WaybillDTO;

@Mapper
public interface ReturnHandleDAO {

	int returnHandleInsert(ReturnHandleDTO h);

	int returnHandleUpdate(ReturnHandleDTO dto);

	List<WaybillDTO> returnHandleList(Map<String, Object> param);

	int returnHandleListTotalPage(Map<String, Object> param);

	int returnHandleDel(int idx);

}
