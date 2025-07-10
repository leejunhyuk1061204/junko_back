package kr.co.junko.admin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.PowerDTO;
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

}
