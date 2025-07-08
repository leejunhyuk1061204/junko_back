package kr.co.junko.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.TemplateDTO;
import kr.co.junko.dto.TemplateVarDTO;

@Service
public class TemplateService {

	@Autowired TemplateDAO dao;
	
	Map<String, Object> result = null;

	// 템플릿 등록
	public boolean templateInsert(TemplateDTO dto) {
		// Http 클라이언트 요청 시 {{}} 가 있으면 무조건 환경 변수로 인식하는 이슈 발생
	    // JetBrains 우회용: [변수] → {{변수}} 로 변환
	    dto.setTemplate_html(dto.getTemplate_html().replaceAll("\\[(.*?)\\]", "{{$1}}"));
		
		int row = dao.templateInsert(dto);
		if (row == 0) {
			return false;
		}
		// 변수 추출 메서드 활용해서 변수들 리스트에 담기
		List<String> variables = extractVariables(dto.getTemplate_html());
		for (String var : variables) {
			TemplateVarDTO varDTO = new TemplateVarDTO();
			varDTO.setTemplate_idx(dto.getTemplate_idx());
			varDTO.setVariable_name(var);
			dao.insertVariable(varDTO);
		}
		return true;
	}

	// 변수 추출 메서드 ( {{변수}})
	private List<String> extractVariables(String template_html) {
		List<String> list = new ArrayList<>();
		// 괄호 안에 들어있는 실제 변수명을 그룹으로 추출
		// Matcher : 문자열 안에서 특정 패턴에 맞는 부분을 찾아주는 도구
		// Pattern : 찾고 싶은 형식 정의
		Matcher m = Pattern.compile("\\{\\{(.*?)\\}\\}").matcher(template_html);
		// html 전체에서 {{...}} 형식으로 된 변수를 찾음.
		while (m.find()) {
			// 괄호 안의 내용만 추출 및 혹시 있을 공백 제거
			String var = m.group(1).trim();
			// 같은 변수가 여러 번 나올 수도 있어서 중복 방지로 한 번만 추가한다고 제한 걸기
			if (!list.contains(var)) list.add(var);
		}
		return list;
	}

	// 템플릿 수정
	public boolean templateUpdate(TemplateDTO dto) {
		
		// Http 클라이언트 요청 시 {{}} 가 있으면 무조건 환경 변수로 인식하는 이슈 발생
	    // JetBrains 우회용: [변수] → {{변수}} 로 변환
	    dto.setTemplate_html(dto.getTemplate_html().replaceAll("\\[(.*?)\\]", "{{$1}}"));
		
		// 기존 템플릿 불러오기
		TemplateDTO before = dao.selectTemplate(dto.getTemplate_idx());
		if (before == null) return false;
		
		// 본문 수정
		int row = dao.templateUpdate(dto);
		if (row == 0) return false;
		
		// 변수 갱신 (기존 삭제 후 새로 생성)
		dao.templateVarDel(dto.getTemplate_idx());
		
		List<String> variables = extractVariables(dto.getTemplate_html());
		for (String var : variables) {
			TemplateVarDTO varDTO = new TemplateVarDTO();
			varDTO.setTemplate_idx(dto.getTemplate_idx());
			varDTO.setVariable_name(var);
			dao.insertVariable(varDTO);
		}
		return true;
	}

	// 템플릿 삭제
	public boolean templateDel(int template_idx) {
		int row = dao.templateDel(template_idx);
		return row>0;
	}

	// 템플릿 리스트
	public List<TemplateDTO> templateList() {
		return dao.templateList();
	}

	// 템플릿 상세보기
	public TemplateDTO templateDetail(int template_idx) {
		return dao.templateDetail(template_idx);
	}

	// 템플릿 변수 리스트
	public List<TemplateVarDTO> templateVarList(int template_idx) {
		return dao.templateVarList(template_idx);
	}

	// 템플릿 미리보기
	public String templatePreview(int template_idx) {
		// 해당 템플릿 가져오기
	    TemplateDTO template = dao.templateDetail(template_idx);
	    if (template == null) {
	    	return null;
    	}

	    // html 원본 템플릿 내용 가져오기
	    String html = template.getTemplate_html();
	    List<String> variables = extractVariables(html);
	    
	    for (String var : variables) {
	    	// html 키에 샘플 데이터 넣기
	        html = html.replace("{{" + var + "}}", "샘플_" + var);
	    }
	    
	    return html;
	}

	// 다른 패키지에서 사용하기 위해 메서드 생성
	public TemplateDTO getTemplate(int template_idx) {
		return dao.selectTemplate(template_idx);
	}

}
