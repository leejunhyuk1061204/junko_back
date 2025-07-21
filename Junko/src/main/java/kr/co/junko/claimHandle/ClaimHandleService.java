package kr.co.junko.claimHandle;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.claim.ClaimService;
import kr.co.junko.dto.ClaimDTO;
import kr.co.junko.dto.ClaimHandleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimHandleService {

	private final ClaimHandleDAO dao;
	private final ClaimService claimService;

	@Transactional
	public boolean claimHandleInsert(ClaimHandleDTO dto) {
		boolean insertResult = dao.claimHandleInsert(dto)>0;
		if(!insertResult) throw new RuntimeException("클레임 처리 등록 실패");
		
		ClaimDTO claimDTO = new ClaimDTO();
		claimDTO.setClaim_idx(dto.getClaim_idx());
		claimDTO.setStatus(dto.getStatus());
		claimDTO.setCustom_idx(dto.getCustom_idx());
		claimDTO.setWarehouse_idx(dto.getWarehouse_idx());
		
		boolean updateResult = claimService.claimUpdate(claimDTO);
		if(!updateResult) throw new RuntimeException("클레임 상태 변경 실패");
		
		return true;
	}

	public boolean claimHandleUpdate(ClaimHandleDTO dto) {
		return dao.claimHandleUpdate(dto)>0;
	}

	public Map<String, Object> claimHandleList(Map<String, Object> param) {
		Map<String, Object>result = new HashedMap<String, Object>();
		if(param.get("page") != null) {
			int cnt = 10;
			int offset =((int)param.get("page")-1)*cnt;
			param.put("cnt", cnt);
			param.put("offset", offset);
			int total = dao.claimHandleListTotalPage(param);
			result.put("total", total);
		}
		List<Map<String, Object>>list = dao.claimHandleList(param);
		result.put("list", list);
		return result;
	}

	public boolean claimHandleDel(int idx) {
		return dao.claimHandleDel(idx)>0;
	} 
}
