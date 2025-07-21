package kr.co.junko.returnReceive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.junko.dto.ReturnProductDTO;
import kr.co.junko.dto.ReturnReceiveDTO;
import kr.co.junko.dto.WaybillDTO;
import kr.co.junko.returnHandle.ReturnHandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReturnReceiveService {
	
	private final ReturnReceiveDAO dao;
	private final ReturnHandleService returnHandleService;
	
	public boolean returnReceiveUpdate(ReturnReceiveDTO dto) {
		
		if("반품완료".equals(dto.getStatus())) {
			return returnHandleService.returnHandleInsert(dto);
		}else {
			return dao.returnReceiveUpdate(dto)>0;
		}
	}

	public Map<String, Object> returnReceiveList(Map<String, Object> param) {
		Map<String, Object>result = new HashMap<String, Object>();
		if(param.get("page") != null) {
			int cnt = 10;
			int offset = ((int)param.get("page")-1)*cnt;
			param.put("cnt", cnt);
			param.put("offset", offset);
			int total = dao.returnReceiveListTotalPage(param);
			result.put("total", total);
		}
		List<Map<String, Object>>list = dao.returnReceiveList(param);
		result.put("list", list);
		return result;
	}

	public boolean returnReceiveDel(int idx) {
		return dao.returnReceiveDel(idx)>0;
	}

	public Map<String, Object> returnReceiveProductList(int idx) {
		Map<String, Object>result = new HashMap<String, Object>();
		List<Map<String, Object>>list = dao.returnReceiveProductList(idx);
		result.put("list", list);
		return result;
	}

}
