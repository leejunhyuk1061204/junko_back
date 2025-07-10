package kr.co.junko.claim;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ClaimDTO;
import kr.co.junko.dto.ReturnProductDTO;

@Mapper
public interface ClaimDAO {

	int claimInsert(ClaimDTO dto);

	int claimUpdate(ClaimDTO dto);

	List<ClaimDTO> claimList(Map<String, Object> param);

	int claimListTotalPage(Map<String, Object> param);

	int claimDel(int claim_idx);

	int returnProductInsert(ReturnProductDTO product);

	int returnProductUpdate(ReturnProductDTO dto);

	List<ReturnProductDTO> returnProductList(Map<String, Object> param);

	int returnProductListTotalPage(Map<String, Object> param);

	int returnProductDel(int idx);

	ClaimDTO claimDetailByIdx(int idx);

	List<ReturnProductDTO> returnProductByClaimIdx(int idx);

}
