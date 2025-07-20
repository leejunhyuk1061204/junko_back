package kr.co.junko.file;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

	private final FileDAO dao;

	@Value("${spring.servlet.multipart.location}") 
	private String root;

	public FileDTO fileSearchByOrderIdx(int order_idx) {
		return dao.fileSearchByOrderIdx(order_idx);
	}

    public boolean uploadFile(String type, int idx, MultipartFile file) {
		try {
			String oriName = file.getOriginalFilename();
			String uuid = UUID.randomUUID().toString();
			String ext = oriName.substring(oriName.lastIndexOf("."));
			String newName = uuid + ext;

			// 디렉토리 자동 생성
			String extDir = ext.replace(".", "");
			File folder = new File(root + "/" + extDir);
			if (!folder.exists()) folder.mkdirs();

			File dest = new File(folder, newName);
			file.transferTo(dest);

			FileDTO dto = new FileDTO();
			dto.setOri_filename(oriName);
			dto.setNew_filename(newName);
			dto.setType(type);
			dto.setIdx(idx);

			int row = dao.insertFile(dto);
			return row > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<FileDTO> fileList(String type, int idx) {
		return dao.fileList(type, idx);
	}

    public boolean delFile(String type, int idx) {
       	Map<String, Object> param = new HashMap<String, Object>();
		param.put("type", type);
		param.put("idx", idx);
		return dao.delFile(param) > 0;
    }

}
