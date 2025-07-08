package kr.co.junko.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ShipmentDTO {

	private int shipment_idx;
	private int user_idx;
	private int sales_idx;
	private int waybill_idx;
	private LocalDate shipment_date;
	private String status;
	private boolean del_yn;
	private int warehouse_idx;
	
	// 출고완료 시 입력되어야 하는 값
	// user_idx (담당자), stock_idx (어떤 물건 사용했는지), stock_cnt 몇개 썼는지\
	// 필수 : product_idx, stock_cnt, zone_idx // user_idx, warehouse_idx
	// 옵션 : product_option_idx, manufacture, expiration
	// product_idx, product_option_idx, manufacture, expiration, stock_cnt, zone_idx 는 stockInfo로
	// user_idx, warehouse_idx 는 따로
	private List<Map<String, Object>>stockInfo;
}
