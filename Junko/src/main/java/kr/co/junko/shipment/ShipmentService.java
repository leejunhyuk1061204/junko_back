package kr.co.junko.shipment;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShipmentService {

	private final ShipmentDAO dao;

	public boolean shipmentInsert(int sales_idx, int waybill_idx) {

		// user_idx, sales_idx, waybill_idx, shipment_date
		
		
		return false;
	}
	
}
