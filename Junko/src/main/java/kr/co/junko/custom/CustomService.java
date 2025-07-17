package kr.co.junko.custom;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.CustomDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomService {
	
	@Autowired CustomDAO dao;
	
	Map<String, Object> result = null;

	public boolean customInsert(CustomDTO dto) {
		int row = dao.customInsert(dto);
		return row>0;
	}

	public boolean customUpdate(CustomDTO dto) {
		int row = dao.customUpdate(dto);
		return row>0;
	}

	public boolean customDel(int custom_idx) {
		int row = dao.customDel(custom_idx);
		return row>0;
	}

	public List<CustomDTO> customList(Map<String, Object> param) {
		int start = 0;
		int size = 10;

		if (param.get("start") != null) {
			start = Integer.parseInt(param.get("start").toString());
		}
		if (param.get("size") != null) {
			size = Integer.parseInt(param.get("size").toString());
		}

		param.put("start", start);
		param.put("size", size);

		return dao.customList(param);
	}

	public int customCnt(Map<String, Object> param) {
		return dao.customCnt(param);
	}

	public CustomDTO customSelect(int custom_idx) {
		return dao.customSelect(custom_idx);
	}

}
