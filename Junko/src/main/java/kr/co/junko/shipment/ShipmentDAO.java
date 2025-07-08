package kr.co.junko.shipment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ShipmentDTO;
import kr.co.junko.dto.WaybillDTO;

@Mapper
public interface ShipmentDAO {

	int shipmentInsert(ShipmentDTO dto);

	int shipmentUpdate(ShipmentDTO dto);

	List<ShipmentDTO> shipmentList(Map<String, Object> param);

	int shipmentListTotalPage(Map<String, Object> param);

	int shipmentDel(int idx);

	ShipmentDTO shipmentDetailByIdx(int shipment_idx);

}
