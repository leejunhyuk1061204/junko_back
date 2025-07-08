package kr.co.junko.claim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.ClaimDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimService {

	private final ClaimDAO dao;

	public boolean claimInsert(ClaimDTO dto) {
		return dao.claimInsert(dto)>0;
	}

	public boolean claimUpdate(ClaimDTO dto) {
		return dao.claimUpdate(dto)>0;
	}

	public Map<String, Object> claimList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<ClaimDTO>list = dao.claimList(param);
		int total = dao.claimListTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public boolean claimDel(int claim_idx) {
		return dao.claimDel(claim_idx)>0;
	}
	
}
