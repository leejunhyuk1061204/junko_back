package kr.co.junko.accountDepartment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.AccountingDepartmentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountDepartmentService {

	@Autowired
	private final AccountDepartmentDAO dao;
	
	
	public List<AccountingDepartmentDTO> accountDeptList(int entry_idx) {
		return dao.accountDeptList(entry_idx);
	}


	public AccountingDepartmentDTO accountDeptDetail(int entry_idx, int dept_idx) {
		return dao.accountDeptDetail(entry_idx, dept_idx);
	}


	public boolean accountDeptAdd(int entry_idx, AccountingDepartmentDTO dto) {
		dto.setEntry_idx(entry_idx);
	    int row = dao.accountDeptAdd(dto);
	    return row > 0;
	}


	public boolean accountDeptUpdate(AccountingDepartmentDTO dto) {
		int row = dao.accountDeptUpdate(dto);
		
		return row > 0;
	}


	public boolean accountDeptDelete(int dept_idx) {
		int row = dao.accountDeptDelete(dept_idx);
		
		return row > 0;
	}
	
}
