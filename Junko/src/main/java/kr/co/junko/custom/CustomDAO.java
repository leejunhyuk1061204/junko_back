package kr.co.junko.custom;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.CustomDTO;

@Mapper
public interface CustomDAO {

	int customInsert(CustomDTO dto);

	int customUpdate(CustomDTO dto);

	int customDel(int custom_idx);

	List<CustomDTO> customList(Map<String, Object> param);

	int customCnt(Map<String, Object> param);

	CustomDTO customSelect(int custom_idx);

	List<Map<String, Object>> customList2(Map<String, Object> param);

	int customListTotalPage(Map<String, Object> param);
	
	
	
}
