package kr.co.junko.document;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.ApprovalLineDTO;
import kr.co.junko.dto.DocumentCreateDTO;
import kr.co.junko.dto.DocumentDTO;
import kr.co.junko.dto.TemplateDTO;
import kr.co.junko.template.TemplateService;

@Service
public class DocumentService {

	@Autowired DocumentDAO dao;
    @Autowired TemplateService temService;
	
	public String documentPreview(int template_idx, Map<String, String> variables) {
		// 해당 템플릿 가져오기
		TemplateDTO template = temService.getTemplate(template_idx);
		if (template == null) {
			return null;
		}
		
		// html 원본 템플릿 내용 가져오기
		String html = template.getTemplate_html();
		if (html == null) {
			return "";
		}
		
		// 맵에 답긴 변수들을 하나씩 꺼내서 템플릿 안의 {{변수}} 를 값으로 치환
		for (Map.Entry<String, String> entry : variables.entrySet()) {
			// 키 가져오기
			String key = entry.getKey();
			// 값 가져오기
			String value = entry.getValue();
			// 템플릿 내의 키에 값을 넣어 치환하기
			html = html.replace("{{"+key+"}}", value);
		}
		
		return html;
	}

	@Transactional
	public Map<String, Object> documentInsert(DocumentCreateDTO dto) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		// 템플릿 가져오기
		TemplateDTO template = temService.getTemplate(dto.getTemplate_idx());
		if (template == null) {
			result.put("success", false);
			return result;
		}
		
		// html 내용 가져오기
		String html = template.getTemplate_html();
		Map<String, String>variables = dto.getVariables();
		for (Map.Entry<String, String> entry : variables.entrySet()) {
			html = html.replace("{{" + entry.getKey() + "}}", entry.getValue());
		}
		
		// 치환된 HTML 에 내용 담아서 문서 생성 준비
		DocumentDTO doc = new DocumentDTO();
		doc.setUser_idx(dto.getUser_idx());
		doc.setContent(html);
		doc.setStatus("결재중");
		
		// 문서 DB 저장
		int row = dao.documentInsert(doc);
		if (row == 0) {
			result.put("success", false);
			return result;
		}
		
		// 저장한 문서 idx 값 가져오기
		int document_idx = doc.getDocument_idx();
		
		// 결재자 리스트를 기반으로 결재 라인에 등록
		int step = 1;
		for (int approval_id : dto.getApprover_ids()) {
			ApprovalLineDTO line = new ApprovalLineDTO();
			line.setDocument_idx(document_idx);
			line.setUser_idx(approval_id);
			line.setStep(step++);
			line.setStatus("미확인");
			dao.insertApprovalLine(line);
			
		}
		
		result.put("success", true);
		result.put("preview", html);
		result.put("document_idx", document_idx);
		
		return result;
	}
	
}
