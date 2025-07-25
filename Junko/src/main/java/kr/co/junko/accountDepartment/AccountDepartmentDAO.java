package kr.co.junko.accountDepartment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

	AccountingDepartmentDTO accountDeptDetailByDeptIdx(int dept_idx);

	int userIdxByLoginId(String loginId);
	
	@Select("SELECT as_idx, as_name FROM accountSubject WHERE del_yn = 0")
	List<Map<String, Object>> getAccountSubjectList();
	
	// 분개 개수 조회 
	int countDeptByEntryIdx(int entry_idx);
	
}
