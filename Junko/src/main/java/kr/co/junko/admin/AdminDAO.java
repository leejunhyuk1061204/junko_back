package kr.co.junko.admin;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.MemberDTO;

@Mapper
public interface AdminDAO {

	int updateJobNdept(MemberDTO dto);

}
