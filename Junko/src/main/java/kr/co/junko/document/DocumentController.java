package kr.co.junko.document;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.DocumentCreateDTO;
import kr.co.junko.dto.TemplatePreviewDTO;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@Slf4j
public class DocumentController {

	@Autowired DocumentService service;
	
	Map<String, Object> result = null;
	
	// 문서 미리보기
	@PostMapping(value="/document/preview")
	public Map<String, Object> documentPreview(@RequestBody TemplatePreviewDTO dto) {
		result = new HashMap<String, Object>();
		log.info("dto : {}",dto);
		
		String html = service.documentPreview(dto.getTemplate_idx(), dto.getVariables());
		result.put("preview", html);
		result.put("success", html != null);
		
		return result;
	}
	
	// 전자 결재 문서 생성
	@PostMapping(value="/document/insert")
	public Map<String, Object>documentInsert(@RequestBody DocumentCreateDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		
		Map<String, Object> insertResult = service.documentInsert(dto);
		result.putAll(insertResult);
		
		return result;
	}
	
}
