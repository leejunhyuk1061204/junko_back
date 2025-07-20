package kr.co.junko.claim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.ClaimDTO;
import kr.co.junko.dto.FullClaimDTO;
import kr.co.junko.dto.ReturnProductDTO;
import kr.co.junko.waybill.WaybillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimService {

	private final ClaimDAO dao;
	private final WaybillService waybillService;

	@Transactional
	public boolean claimInsert(FullClaimDTO dto) {
		
		ClaimDTO claimDTO = dto.getClaim();
		List<ReturnProductDTO> productDTO = dto.getProducts();
		boolean claimResult = dao.claimInsert(claimDTO)>0;
		if(!claimResult) throw new RuntimeException("클레임 등록 실패");
		
		log.info("claim_idx : " + claimDTO.getClaim_idx());
		
		for(ReturnProductDTO product : productDTO) {
			product.setClaim_idx(claimDTO.getClaim_idx());
			boolean productResult = dao.returnProductInsert(product)>0;
			if(!productResult) throw new RuntimeException("반품 상품 등록 실패");
		}
		
		return true;
	}

	public boolean claimUpdate(ClaimDTO dto) {
		if("처리완료".equals(dto.getStatus())) {
			return waybillService.returnWaybillInsert(dto);
		}else {
			return dao.claimUpdate(dto)>0;
		}
	}

	public Map<String, Object> claimList(Map<String, Object> param) {
		Map<String, Object>result = new HashMap<String, Object>();
		if(param.get("page") != null) {
			int cnt = 10;
			int offset = ((int)param.get("page")-1)*cnt;
			param.put("cnt", cnt);
			param.put("offset", offset);
			int total = dao.claimListTotalPage(param);
			result.put("total", total);
		}
		List<Map<String, Object>>list = dao.claimList(param);
		result.put("list", list);
		return result;
	}

	public boolean claimDel(int claim_idx) {
		return dao.claimDel(claim_idx)>0;
	}

	public boolean returnProductUpdate(ReturnProductDTO dto) {
		return dao.returnProductUpdate(dto)>0;
	}

	public Map<String, Object> returnProductList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<ReturnProductDTO>list = dao.returnProductList(param);
		int total = dao.returnProductListTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public boolean returnProductDel(int idx) {
		return dao.returnProductDel(idx)>0;
	}

	public ClaimDTO claimDetailByIdx(int idx) {
		return dao.claimDetailByIdx(idx);
	}

	public List<ReturnProductDTO> returnProductByClaimIdx(int idx) {
		return dao.returnProductByClaimIdx(idx);
	}
	
}
