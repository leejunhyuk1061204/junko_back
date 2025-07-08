package kr.co.junko.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import kr.co.junko.dto.ApprovalLineDTO;
import kr.co.junko.dto.ApprovalLogDTO;
import kr.co.junko.dto.DocumentCreateDTO;
import kr.co.junko.dto.DocumentDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.TemplateDTO;
import kr.co.junko.file.FileDAO;
import kr.co.junko.template.TemplateService;

@Service
public class DocumentService {

	@Autowired DocumentDAO dao;
    @Autowired TemplateService temService;
    @Autowired FileDAO filedao;
	
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

	// PDF 파일 생성 및 파일 테이블 저장
	@Transactional
	public String documentPDF(DocumentDTO dto) throws Exception {

		// 문서 조회
		DocumentDTO doc = dao.documentIdx(dto.getDocument_idx());
		if (doc == null || doc.getContent() == null || doc.getContent().trim().isEmpty()) {
			throw new IllegalArgumentException("문서 내용이 비어있습니다.");
		}
		
		String htmlContent = doc.getContent().trim();
		
		// <html> 이나 <body> 태그가 있는지 검사
		String lower = htmlContent.toLowerCase();
		boolean hasHtml = lower.contains("<html");
		boolean hasBody = lower.contains("<body");
		if (!hasHtml || !hasBody) {
			htmlContent = "<html><body>" + htmlContent + "</body></html>";
		}
		
		// 한글 폰트를 강제로 CSS에 삽입
		String css = "<style> * { font-family: 'malgun'; } </style>";
		htmlContent = htmlContent.replaceFirst("(?i)<head>", "<head>" + css);
		if (!htmlContent.toLowerCase().contains("<head>")) {
			htmlContent = htmlContent.replaceFirst("(?i)<html>", "<html><head>" + css + "</head>");
		}
		
		// 파일 경로 생성
		String uploadRoot = "C:/upload/pdf";
		new File(uploadRoot).mkdirs(); // 폴더 없으면 생성
		String fileName = "document_"+UUID.randomUUID().toString().substring(0, 8)+".pdf";
		String filePath = Paths.get(uploadRoot, fileName).toString();
		
		// PDF 생성 (HTML 렌더링)
		try (OutputStream os = new FileOutputStream(filePath)){
			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.useFastMode();
			builder.withHtmlContent(htmlContent, null);
			
			// 한글 폰트 경로 (윈도우 기준)
			String fontPath = "C:/Windows/Fonts/malgun.ttf";
			builder.useFont(new File("C:/Windows/Fonts/malgun.ttf"), "malgun");

			builder.toStream(os);
			builder.run();
		}
		
		// 파일 테이블에 저장
		FileDTO file = new FileDTO();
		file.setOri_filename("문서 PDF");
		file.setNew_filename(fileName);
		file.setType("document");
		file.setIdx(dto.getDocument_idx());
		filedao.insertFile(file);
		
		return filePath;
	}

	@Transactional
	public boolean documentApprove(Map<String, Object> req) {
		int document_idx = (int) req.get("document_idx");
		int user_idx = (int) req.get("user_idx");
		String comment = (String) req.getOrDefault("comment", "");
		
		ApprovalLineDTO line = dao.documentApprove(document_idx, user_idx);
		if (line == null || !"미확인".equals(line.getStatus())) {
			return false;
		}
		
		// 순서 보장
		int minStep = dao.getMinStep(document_idx);
		if (line.getStep() != minStep) {
			return false; // 본인 차례 아직임.
		}
		
		line.setStatus("승인");
		line.setComment(comment);
		line.setApproved_date(new Date(System.currentTimeMillis()));
		dao.updateApprove(line);
		
	    ApprovalLogDTO log = new ApprovalLogDTO();
	    log.setDocument_idx(document_idx);
	    log.setUser_idx(user_idx);
	    log.setStatus("승인");
	    log.setComment(comment);
	    log.setApproved_date(new Date(System.currentTimeMillis()));
	    dao.insertLog(log);
		
		boolean allApproved = dao.approveCnt(document_idx) == 0;
		if (allApproved) {
			dao.updateDocStatus(document_idx, "승인");
		}
		return true;
	}

	@Transactional
	public boolean documentReject(Map<String, Object> req) {
		int document_idx = (int) req.get("document_idx");
		int user_idx = (int) req.get("user_idx");
		String comment = (String) req.getOrDefault("comment", "");
		
		ApprovalLineDTO line = dao.documentApprove(document_idx, user_idx);
		if (line == null || !"미확인".equals(line.getStatus())) {
			return false;
		}
		
		// 순서 보장
		int minStep = dao.getMinStep(document_idx);
		if (line.getStep() != minStep) {
			return false; // 본인 차례 아직임.
		}
		
		line.setStatus("반려");
		line.setComment(comment);
		line.setApproved_date(new Date(System.currentTimeMillis()));
		dao.updateApprove(line);
		
	    ApprovalLogDTO log = new ApprovalLogDTO();
	    log.setDocument_idx(document_idx);
	    log.setUser_idx(user_idx);
	    log.setStatus("반려");
	    log.setComment(comment);
	    log.setApproved_date(new Date(System.currentTimeMillis()));
	    dao.insertLog(log);

		dao.updateDocStatus(document_idx, "반려");
		
		return true;
	}

	public String getDocStatus(int document_idx) {
		return dao.getDocStatus(document_idx);
	}
	
}
