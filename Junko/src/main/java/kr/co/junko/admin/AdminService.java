package kr.co.junko.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.PowerDTO;
import kr.co.junko.dto.TemplateDTO;
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

	public boolean insertPower(Map<String, Object> param) {
		int row = dao.insertPower(param);
		return row>0;
	}

	public boolean grantPower(int user_idx, int power_idx) {
		int row = dao.grantPower(user_idx, power_idx);
		return row>0;
	}

	public List<PowerDTO> powerList(int user_idx) {
		return dao.powerList(user_idx);
	}

	public boolean updatePowerAll(int user_idx, List<Integer> power_list) {
		dao.delUserPower(user_idx); // 기존 권한 전부 삭제
		for (int power_idx : power_list) {
			dao.grantPower(user_idx, power_idx);
		}
		return true;
	}

	public List<MemberDTO> getUserPower(int power_idx) {
		return dao.getUserPower(power_idx);
	}

	public boolean updatePower(int power_idx, String power_name) {
		int row = dao.updatePower(power_idx, power_name);
		return row>0;
	}

	public boolean delPower(int power_idx) {
		int row = dao.delPower(power_idx);
		return row>0;
	}
	
	public boolean resignUpdate(Map<String, Object> param) {
		return dao.resignUpdate(param)>0;
	}

	public boolean empUpdate(Map<String, Object> param) {
		return dao.empUpdate(param)>0;
	}
	
	public List<Map<String, Object>> deptTree() {
		List<Map<String, Object>> deptList = dao.deptTree();
	    for (Map<String, Object> dept : deptList) {
	        int deptIdx = (int) dept.get("dept_idx");
	        List<Map<String, Object>> users = dao.allUserList(deptIdx);
	        dept.put("users", users);
	    }
	    return deptList;
	}

	public Map<String, Object> userList(Map<String, Object> param) {
		int page = (int) param.getOrDefault("page", 1);
	    int size = (int) param.getOrDefault("size", 10);
	    String dept_name = param.getOrDefault("dept_name", "").toString();
	    String search = param.getOrDefault("search", "").toString();
	    String sort = param.getOrDefault("sort", "hire_date ASC").toString();
	    
	    List<String> allowedSortColumns = List.of("user_name", "hire_date", "dept_idx");
	    String sortColumn = "hire_date";
	    String sortDirection = "ASC";

	    if (page < 1) page = 1;
	    if (size < 1) size = 10;

	    String[] sortParts = sort.split("\\s+");
	    if (sortParts.length > 0 && allowedSortColumns.contains(sortParts[0])) {
	        sortColumn = sortParts[0];
	    }
	    if (sortParts.length > 1 && ("DESC".equalsIgnoreCase(sortParts[1]) || "ASC".equalsIgnoreCase(sortParts[1]))) {
	        sortDirection = sortParts[1].toUpperCase();
	    }
	    String finalSort = sortColumn + " " + sortDirection;

	    int offset = (page - 1) * size;

	    Map<String, Object> queryParam = new HashMap<>();
	    queryParam.put("dept_name", dept_name);
	    queryParam.put("search", search);
	    queryParam.put("limit", size);
	    queryParam.put("offset", offset);
	    queryParam.put("sort", finalSort);
	    
	    List<Map<String, Object>> list = dao.userList(queryParam);
	    int total = dao.userTotalCnt(queryParam);
	    

	    Map<String, Object> result = new HashMap<>();
	    result.put("list", list);
	    result.put("total", total);
	    result.put("page", page);
	    result.put("size", size);
	    
	    log.info("==================리스트 결과 수"+list.size());
	    log.info("======================총 카운트 total"+total);

	    return result;
	}
	
	public Map<String, Object> userDetail(int user_idx) {
	    return dao.userDetail(user_idx);
	}

	public int revokeGrant() {
		return dao.revokeGrant();
	}

	public List<Map<String, Object>> allUserList() {
		return dao.allUserList();
	}

	public List<Map<String, Object>> getDeptList() {
		return dao.getDeptList();
	}


}
