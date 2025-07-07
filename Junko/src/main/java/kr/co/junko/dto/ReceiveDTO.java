package kr.co.junko.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ReceiveDTO {

	private int receive_idx;
	private int order_idx;
	private int user_idx;
	private LocalDate receive_date;
	private String status;
	private boolean del_yn;
	
	// 입고 완료 시 입력받는 값
	private List<Map<String, Object>>stockInfo;
	/*
	 [
	 { product_idx:1, product_option_idx : 1, stock_cnt:15, zone_idx : 1, manufacture : '', expiration: '' },
	 { product_idx:1, product_option_idx : 1, stock_cnt:15, zone_idx : 1, manufacture : '', expiration: '' },
	 { product_idx:1, product_option_idx : 1, stock_cnt:15, zone_idx : 1, manufacture : '', expiration: '' },
	 { product_idx:1, stock_cnt:15, zone_idx : 1 },
	 ]
	 user_idx도 입력 받지만 list에는 안들어감
	  product_option_idx, manufacture, expiration 는 입력 받을 수도 있고 아닐 수도 있고
	 */
	 
	
}
