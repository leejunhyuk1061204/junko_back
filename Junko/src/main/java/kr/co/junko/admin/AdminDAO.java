package kr.co.junko.admin;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.PowerDTO;

@Mapper
public interface AdminDAO {

	int updateJobNdept(MemberDTO dto);

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
	
	List<Map<String, Object>> userList(int dept_idx);

	Map<String, Object> userDetail(int user_idx);

	int revokeGrant();


}
