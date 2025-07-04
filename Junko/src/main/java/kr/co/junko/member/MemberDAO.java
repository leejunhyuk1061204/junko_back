package kr.co.junko.member;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.MemberDTO;

@Mapper
public interface MemberDAO {

	int join(MemberDTO dto);

	int overlay(String id);

	int login(MemberDTO dto);

}
