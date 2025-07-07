package kr.co.junko.dto;

import lombok.Data;

@Data
public class ZoneDTO {

	private int zone_idx;
	private int warehouse_idx;
	private String zone_name;
	private boolean del_yn;
	
}
