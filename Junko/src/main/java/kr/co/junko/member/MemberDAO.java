package kr.co.junko.member;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.AdminLogDTO;
import kr.co.junko.dto.MemberDTO;

@Mapper
public interface MemberDAO {

	int join(MemberDTO dto);

	int overlay(String id);

	MemberDTO login(MemberDTO dto);

	int getUserIdxById(String loginId);
	
	int insertAdminLog(AdminLogDTO log);

	String findPw(Map<String, Object> param);

	int pwUpdate(String user_id, String new_pw);

	List<MemberDTO> userList(Map<String, Object> param);

	int userListTotalPage(Map<String, Object> param);


}
