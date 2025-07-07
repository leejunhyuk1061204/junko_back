package kr.co.junko.accountDepartment;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.AccountingDepartmentDTO;
import kr.co.junko.dto.FileDTO;
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


	public void accountDeptFile(MultipartFile file, String string, int dept_idx) throws IllegalStateException, IOException {
		
	    String paths = "c:/upload"; 
	    File files = new File(paths);
	    if (!files.exists()) files.mkdirs();

	    
	    String ori_filename = file.getOriginalFilename();
	    String new_filename = System.currentTimeMillis() + "_" + UUID.randomUUID() + "_" + ori_filename;
	    File ext = new File(files, new_filename);
	    file.transferTo(ext);

	   
	    FileDTO dto = new FileDTO();
	    dto.setOri_filename(ori_filename);
	    dto.setNew_filename(new_filename);
	    dto.setReg_date(LocalDateTime.now());
	    dto.setType("ENTRY_DETAIL");
	    dto.setIdx(dept_idx);
	    dto.setDel_yn(false);

	    dao.accountDeptFile(dto);
		
	}


	public FileDTO deptfileDown(int file_idx) {
		return dao.deptfileDown(file_idx);
	}
	
}
