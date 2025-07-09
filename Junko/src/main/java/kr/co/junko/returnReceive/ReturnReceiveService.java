package kr.co.junko.returnReceive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.ReturnReceiveDTO;
import kr.co.junko.dto.WaybillDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReturnReceiveService {
	
	private final ReturnReceiveDAO dao;

	public boolean returnReceiveUpdate(ReturnReceiveDTO dto) {
		return dao.returnReceiveUpdate(dto)>0;
	}

	public Map<String, Object> returnReceiveList(Map<String, Object> param) {
		int cnt = 10;
		int offset = ((int)param.get("page")-1)*cnt;
		param.put("cnt", cnt);
		param.put("offset", offset);
		List<WaybillDTO>list = dao.returnReceiveList(param);
		int total = dao.returnReceiveListTotalPage(param);
		Map<String, Object>result = new HashMap<String, Object>();
		result.put("list", list);
		result.put("total", total);
		result.put("page", param.get("page"));
		return result;
	}

	public boolean returnReceiveDel(int idx) {
		return dao.returnReceiveDel(idx)>0;
	}

}
