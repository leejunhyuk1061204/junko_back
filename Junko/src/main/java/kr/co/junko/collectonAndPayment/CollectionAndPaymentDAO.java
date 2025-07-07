package kr.co.junko.collectonAndPayment;

import java.time.LocalDate;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.CollectionAndPaymentRequestDTO;
import kr.co.junko.dto.CollectionAndPaymentResponseDTO;

@Mapper
public interface CollectionAndPaymentDAO {

	boolean capRegist(String type, LocalDate date, int amount, int custom_idx);

	void insert(CollectionAndPaymentRequestDTO dto);

	CollectionAndPaymentResponseDTO capList(int cap_idx);

	void capUpdate(CollectionAndPaymentRequestDTO dto);

	int capDel(int cap_idx);

}
