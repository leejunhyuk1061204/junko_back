package kr.co.junko.schedule;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.ScheduleDTO;

@Mapper
public interface ScheduleDAO {

	int userIdxByLoginId(String loginId);
	
	String getLabelName(int label_idx);

	int scheduleInsert(ScheduleDTO dto);

	int scheduleUpdate(ScheduleDTO dto);

	int scheduleDelete(int schedule_idx, int user_idx);

	List<Map<String, Object>> scheduleListPersonal(Map<String, Object> param);

	List<Map<String, Object>> scheduleListDept(Map<String, Object> param);

	MemberDTO getUserInfo(int user_idx);

	List<Map<String, Object>> scheduleListStatus(Map<String, Object> param);
	

}
