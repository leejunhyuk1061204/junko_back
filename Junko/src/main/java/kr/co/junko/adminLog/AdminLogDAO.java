package kr.co.junko.adminLog;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.AdminLogDTO;

@Mapper
public interface AdminLogDAO {

	void saveLog(AdminLogDTO dto);

	List<Map<String, Object>> logList(Map<String, Object> param);

}
