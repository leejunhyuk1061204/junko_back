package kr.co.junko.accountDepartment;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.AccountingDepartmentDTO;
import kr.co.junko.dto.FileDTO;

@Mapper
public interface AccountDepartmentDAO {

	List<AccountingDepartmentDTO> accountDeptList(int entry_idx);

	AccountingDepartmentDTO accountDeptDetail(int entry_idx, int dept_idx);

	int accountDeptAdd(AccountingDepartmentDTO dto);

	int accountDeptUpdate(AccountingDepartmentDTO dto);

	int accountDeptDelete(int dept_idx);

	void accountDeptFile(FileDTO dto);

	FileDTO deptfileDown(int file_idx);

}
