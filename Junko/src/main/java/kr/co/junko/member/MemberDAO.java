package kr.co.junko.member;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.memberDTO;

@Mapper
public interface MemberDAO {

	int join(memberDTO dto);

	int overlay(String id);

	int login(memberDTO dto);

}
