package kr.co.junko.member;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.memberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberDAO dao;

	public boolean join(memberDTO dto) {
		int row = dao.join(dto);
		return row>0;
	}

	public boolean overlay(String id) {
		int row = dao.overlay(id);
		return row == 0 ;
	}

	public boolean login(memberDTO dto) {
		int row = dao.login(dto);
		return row > 0 ;
	}

}
