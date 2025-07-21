package kr.co.junko.claimHandle;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ClaimHandleDTO;

@Mapper
public interface ClaimHandleDAO {

	int claimHandleInsert(ClaimHandleDTO dto);

	int claimHandleUpdate(ClaimHandleDTO dto);

	List<Map<String, Object>> claimHandleList(Map<String, Object> param);

	int claimHandleListTotalPage(Map<String, Object> param);

	int claimHandleDel(int idx);

}
