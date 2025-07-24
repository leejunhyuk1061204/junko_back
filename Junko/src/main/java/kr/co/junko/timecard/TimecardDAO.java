package kr.co.junko.timecard;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.junko.dto.TimecardDTO;

@Mapper
public interface TimecardDAO {

//	int attendInsert(@Param("user_idx") int user_idx, @Param("status") String status);
//
//	int endTimeInsert(@Param("user_idx") int user_idx, @Param("status") String status);
//
//	int leaveStatusInsert(@Param("user_idx") int user_idx, @Param("status") String status);
//
//	int checkTodayAttend(@Param("user_idx") int user_idx, @Param("list") List<String> statusList);
//
//	List<Map<String, Object>> attendList(int user_idx);
//
//	List<Map<String, Object>> attendListDept(int dept_idx);
//
//	List<Map<String, Object>> attendListDate(Map<String, Object> param);

	List<Integer> getUserIdxList();

	int searchTimecardByIdx(int idx);

	int timecardInsert(TimecardDTO paramDTO);
	
	int timecardUpdate(TimecardDTO paramDTO);

	int timecardListTotalPage(Map<String, Object> param);

	List<Map<String, Object>> timecardList(Map<String, Object> param);


}
