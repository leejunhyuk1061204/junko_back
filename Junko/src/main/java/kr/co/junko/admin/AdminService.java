package kr.co.junko.admin;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
	
	private final AdminDAO dao;

	public boolean updateJobNdept(MemberDTO dto) {
		int row = dao.updateJobNdept(dto);
		return row>0;
	}

}
