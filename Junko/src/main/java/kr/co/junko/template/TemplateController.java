package kr.co.junko.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.TemplateDTO;
import kr.co.junko.dto.TemplateVarDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin
public class TemplateController {

	@Autowired TemplateService service;
	
	Map<String, Object> result = null;
	
	// 템플릿 생성
	@PostMapping(value="/template/insert")
	public Map<String, Object> templateInsert(@RequestBody TemplateDTO dto){
        log.info("insertTemplate dto : {}", dto);
        boolean success = service.templateInsert(dto);

        result = new HashMap<String, Object>();
        result.put("success", success);
        result.put("templateIdx", dto.getTemplate_idx());
        return result;
	}
	
	// 템플릿 수정
	@PutMapping(value="/template/update")
	public Map<String, Object> templateUpdate(@RequestBody TemplateDTO dto){
		log.info("updateTemplate dto : {}", dto);
		boolean success = service.templateUpdate(dto);
		
		result = new HashMap<String, Object>();
		result.put("success", success);
		result.put("templateIdx", dto.getTemplate_idx());
		
		return result;
	}
	
	// 템플릿 삭제
	@PutMapping(value="/template/del")
	public Map<String, Object> templateDel(@RequestParam int template_idx){
		log.info("delTemplate : {}", template_idx);
		result = new HashMap<String, Object>();
		
		boolean success = service.templateDel(template_idx);
		result.put("success", success);
		
		return result;
	}
	
	// 템플릿 리스트
	@GetMapping(value="/template/list")
	public Map<String, Object> templateList(){
		result = new HashMap<String, Object>();
		
		List<TemplateDTO> list = service.templateList();
		result.put("list", list);
		return result;
	}
	
	// 템플릿 상세보기
	@GetMapping(value="/template/detail")
	public Map<String, Object>templateDetail(@RequestParam int template_idx){
		result = new HashMap<String, Object>();
		
		TemplateDTO detail = service.templateDetail(template_idx);
		result.put("detail", detail);
		return result;
	}
	
	// 변수 목록 조회
	@GetMapping(value="/template/var/list")
	public Map<String, Object> templateVarList(@RequestParam int template_idx){
		result = new HashMap<String, Object>();
		
		List<TemplateVarDTO> list = service.templateVarList(template_idx);
		result.put("list", list);
		
		return result;
	}
	
	// 템플릿 미리보기
	@GetMapping(value="/template/preview/{template_idx}")
	public Map<String, Object> templatePreview(@PathVariable int template_idx){
		result = new HashMap<String, Object>();
		
		String html = service.templatePreview(template_idx);
		result.put("preview", html);
	    result.put("success", html != null);
	    
		return result;
	}
	
}
