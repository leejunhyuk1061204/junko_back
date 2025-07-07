package kr.co.junko.collectonAndPayment;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.CollectionAndPaymentRequestDTO;
import kr.co.junko.dto.CollectionAndPaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectionAndPaymentService {

	@Autowired
	private final CollectionAndPaymentDAO dao;
	Map<String, Object> result = null;
	
	public int capRegist(CollectionAndPaymentRequestDTO dto) {
		boolean exites = dao.capRegist(
				dto.getType(),dto.getDate(),dto.getAmount(),dto.getCustom_idx()
				);
		
	if (exites) {
		throw new IllegalArgumentException("동일한 거래 내역이 이미 존재합니다.");
	}
				
		dao.insert(dto);
		return dto.getCap_idx();
		
	}

	public CollectionAndPaymentResponseDTO capList(int cap_idx) {
		return dao.capList(cap_idx);
	}

	public boolean capUpdate(CollectionAndPaymentRequestDTO dto) {
		    boolean exists = dao.capRegist(dto.getType(), dto.getDate(), dto.getAmount(), dto.getCustom_idx());

		    if (exists) return false;

		    dao.capUpdate(dto);
		    return true;
		}

	public boolean capDel(int cap_idx) {
		return dao.capDel(cap_idx) > 0;
	}

	
}
