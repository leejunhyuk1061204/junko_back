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

}
