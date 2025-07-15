package kr.co.junko.file;

import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

	private final FileDAO dao;

	public FileDTO fileSearchByOrderIdx(int order_idx) {
		return dao.fileSearchByOrderIdx(order_idx);
	}
	
}
