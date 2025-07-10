package kr.co.junko.adminLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.AdminLogDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminLogService {
	
	private final AdminLogDAO dao;
	
	public void saveLog(int admin_idx, String log_type, String target_table, String operation_detail, String ip_address) {
        AdminLogDTO dto = new AdminLogDTO();
        dto.setAdmin_idx(admin_idx);
        dto.setLog_type(log_type);
        dto.setTarget_table(target_table);
        dto.setOperation_detail(operation_detail);
        dto.setLog_time(LocalDateTime.now());
        dto.setIp_address(ip_address);
        
        dao.saveLog(dto); // DB 저장
    }

	public List<Map<String, Object>> logList(Map<String, Object> param) {
		return dao.logList(param);
	}

}
