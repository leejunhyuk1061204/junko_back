package kr.co.junko.adminLog;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.AdminLogDTO;

@Mapper
public interface AdminLogDAO {

	void insertAdminLog(AdminLogDTO dto);

}
