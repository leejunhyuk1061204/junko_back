package kr.co.junko.receive;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ReceiveDTO;
import kr.co.junko.dto.ReceiveProductDTO;

@Mapper
public interface ReceiveDAO {

	int insertReceive(ReceiveDTO dto);

	int insertReceiveProduct(ReceiveProductDTO dto);

	List<Map<String, Object>> receiveInfo(int order_idx);

	int receiveUpdate(ReceiveDTO dto);

	int receiveProductUpdate(ReceiveProductDTO dto);
	
	List<Map<String, Object>> receiveList(Map<String, Object> param);

	int receiveTotalPage(Map<String, Object> param);

	int receiveDel(int idx);

	int receiveProductDel(int idx);

	List<Map<String, Object>> receiveProductList(Map<String, Object> param);

	int receiveProductTotalPage(Map<String, Object> param);


}
