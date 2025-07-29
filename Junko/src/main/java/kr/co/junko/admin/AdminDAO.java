package kr.co.junko.admin;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.PowerDTO;

@Mapper
public interface AdminDAO {

	int insertPower(Map<String, Object> param);

	int grantPower(int user_idx, int power_idx);

	List<PowerDTO> powerList(int user_idx);

	void delUserPower(int user_idx);

	List<MemberDTO> getUserPower(int power_idx);

	int updatePower(int power_idx, String power_name);

	int delPower(int power_idx);
	
	int resignUpdate(Map<String, Object> param);

	int empUpdate(Map<String, Object> param);

	List<Map<String, Object>> deptTree();
	
	List<Map<String, Object>> userList(Map<String, Object> queryParam);

	Map<String, Object> userDetail(int user_idx);

	int revokeGrant();

	List<Map<String, Object>> allUserList();

	int userTotalCnt(Map<String, Object> queryParam);

	List<Map<String, Object>> allUserList(int deptIdx);

	List<Map<String, Object>> getDeptList();

	List<Map<String, Object>> getJobList();

	List<Map<String, Object>> getStatusList();

}
