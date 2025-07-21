package kr.co.junko.file;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.FileDTO;

@Mapper
public interface FileDAO {

	int insertFile(FileDTO dto);

	FileDTO selectTypeIdx(Map<String, Object> param);

	int orderFileUpdate(int order_idx,String fileName);

	FileDTO fileSearchByOrderIdx(int order_idx);

	List<FileDTO> fileList(String type, int idx);

    int delFile(Map<String, Object> param);

	FileDTO downloadFileFileIdx(int file_idx);

	FileDTO latestPdfFile(Map<String, Object> param);
	
}
