package kr.co.junko.file;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.FileDTO;

@Mapper
public interface FileDAO {

	int insertFile(FileDTO dto);

	FileDTO selectTypeIdx(Map<String, Object> param);
	
}
