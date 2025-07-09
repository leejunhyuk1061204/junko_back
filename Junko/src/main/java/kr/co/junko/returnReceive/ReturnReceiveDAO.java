package kr.co.junko.returnReceive;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ReturnReceiveDTO;

@Mapper
public interface ReturnReceiveDAO {

	int returnReceiveInsert(ReturnReceiveDTO returnReceiveDTO);

}
