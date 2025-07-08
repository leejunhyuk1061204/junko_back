package kr.co.junko.file;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.FileDTO;

@Mapper
public interface FileDAO {

	int insertFile(FileDTO dto);
	
}
