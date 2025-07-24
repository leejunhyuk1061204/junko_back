package kr.co.junko.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import kr.co.junko.dto.ApprovalLineDTO;
import kr.co.junko.dto.ApprovalLogDTO;
import kr.co.junko.dto.DocumentCreateDTO;
import kr.co.junko.dto.DocumentDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.MemberDTO;
import kr.co.junko.dto.MsgDTO;
import kr.co.junko.dto.TemplateDTO;
import kr.co.junko.file.FileDAO;
import kr.co.junko.msg.MsgService;
import kr.co.junko.template.TemplateService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentService {

	@Autowired DocumentDAO dao;
    @Autowired TemplateService temService;
    @Autowired FileDAO filedao;
    @Autowired MsgService msgService;
    
    @Value("${spring.servlet.multipart.location}") 
    private String root;
	
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
		doc.setStatus("미확인");
		doc.setType(dto.getType());
		doc.setIdx(dto.getIdx());
		doc.setTemplate_idx(dto.getTemplate_idx());
		
		// 문서 DB 저장
		int row = dao.documentInsert(doc);
		
		if (row == 0) {
			result.put("success", false);
			return result;
		}
		
		// 저장한 문서 idx 값 가져오기
		int document_idx = doc.getDocument_idx();
		
		// 변수 document_variable 에 저장
		if (variables != null) {
		    for (Map.Entry<String, String> entry : variables.entrySet()) {
		        Map<String, Object> param = new HashMap<>();
		        param.put("document_idx", document_idx);
		        param.put("key", entry.getKey());
		        param.put("value", entry.getValue());
		        dao.insertDocumentVar(param);
		    }
		}
		
		// 결재자 리스트를 기반으로 결재 라인에 등록
		int step = 1;
		Integer firstApproverId = null;
		
		if(dto.getApprover_ids() != null) {
			for (int approval_id : dto.getApprover_ids()) {
				
				if (step == 1) firstApproverId = approval_id;
				
				ApprovalLineDTO line = new ApprovalLineDTO();
				line.setDocument_idx(document_idx);
				line.setUser_idx(approval_id);
				line.setStep(step++);
				line.setStatus("미확인");
				dao.insertApprovalLine(line);
				
				step++;
			}
		}
		
		// 쪽지 자동 발송 (1단계 결재자 한정)
		if (firstApproverId != null) {
			MsgDTO msg = new MsgDTO();
			msg.setSender_idx(dto.getUser_idx());
			msg.setReceiver_idx(firstApproverId);
			msg.setMsg_title("[전자결재] "+template.getTemplate_name()+" 결재 요청");
			msg.setMsg_content("전자결재 문서 [ "+template.getTemplate_name()+" ]에 대한 결재 요청이 도착했습니다.");
			log.info("=============================msg : {}", msg);
			msgService.msgInsert(msg);
		}
		
		result.put("success", true);
		result.put("preview", html);
		result.put("document_idx", document_idx);
		
		return result;
	}
	
	public List<MemberDTO> searchUser(String user_name) {
		return dao.searchUser(user_name);
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
		String uploadRoot = root+"/pdf";
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
		
	    String status = dao.getDocStatus(document_idx);
	    if ("승인".equals(status) || "반려".equals(status)) {
	        return false;
	    }
		
		ApprovalLineDTO line = dao.documentApprove(document_idx, user_idx);
		if (line == null || !"미확인".equals(line.getStatus())) {
			return false;
		}
		
		// 순서 보장
		int minStep = dao.getMinStep(document_idx);
		if (line.getStep() != minStep) {
			return false; // 본인 차례 아직임.
		}
		
		String docStatus = dao.getDocStatus(document_idx);
		if ("미확인".equals(docStatus)) {
			dao.updateDocStatus(document_idx, "결재중");
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
		
	    String status = dao.getDocStatus(document_idx);
	    if ("승인".equals(status) || "반려".equals(status)) {
	        return false;
	    }
	    
		if (comment == null || comment.trim().isEmpty()) {
			return false;
		}
		
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

	public List<ApprovalLogDTO> getApprovalLogs(int document_idx) {
		return dao.getApprovalLogs(document_idx);
	}

	public String documentDOCX(int document_idx) {
		DocumentDTO doc = dao.documentIdx(document_idx);
		if (doc == null || doc.getContent() == null || doc.getContent().trim().isEmpty()) {
			throw new IllegalArgumentException("문서 내용이 비어있습니다.");
		}
		
		String html = doc.getContent().trim();
		
		String uploadRoot = root+"/docx";
		new File(uploadRoot).mkdirs();
		String fileName = "document_"+UUID.randomUUID().toString().substring(0,8)+".docx";
		String filePath = Paths.get(uploadRoot, fileName).toString();
		
		try (FileOutputStream out = new FileOutputStream(filePath)){
			XWPFDocument document = new XWPFDocument();
			
			String plainText = Jsoup.parse(html).text();
			
			XWPFParagraph paragraph = document.createParagraph();
			XWPFRun run = paragraph.createRun();
			run.setText(plainText);
			
			document.write(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FileDTO file = new FileDTO();
		file.setOri_filename("문서 DOCX");
		file.setNew_filename(fileName);
		file.setType("document");
		file.setIdx(document_idx);
		filedao.insertFile(file);
		
		return filePath;
	}

	public List<DocumentDTO> documentList(Map<String, Object> param) {
		String tab = (String) param.get("tab");
		
		List<DocumentDTO> list = null;
		
		if (tab == null || tab.equals("") || tab.equals("상신함")) {
		    list = dao.requestedDocument(param);
		} else if (tab.equals("수신함")) {
		    list = dao.receivedDocument(param);
		}
		
		for (DocumentDTO doc : list) {
			List<Map<String, String>> vars = dao.getVariables(doc.getDocument_idx());
			
			// List -> Map 으로 변환
			Map<String, String> map = new HashMap<String, String>();
			for (Map<String, String> item : vars) {
				map.put(item.get("key"), item.get("value"));
			}
			doc.setVariables(map);
		}
		return list;
	}

	public int documentCnt(Map<String, Object> param) {
		return dao.documentCnt(param);
	}

    public DocumentDTO getByTypeAndIdx(String type, int idx) {
		Map<String, Object> map = new HashMap<>();
		map.put("type", type);
		map.put("idx", idx);
		return dao.getByTypeAndIdx(map);
    }

	public boolean documentUpdate(DocumentCreateDTO dto) {
	    // 1. 문서 기본 정보 업데이트
	    int updated = dao.documentUpdate(dto);
	    if (updated == 0) return false;

	    // 2. 기존 변수 삭제
	    dao.delDocumentVar(dto.getDocument_idx());

	    // 3. 새 변수 등록
	    for (Map.Entry<String, String> entry : dto.getVariables().entrySet()) {
	        Map<String, Object> param = new HashMap<>();
	        param.put("document_idx", dto.getDocument_idx());
	        param.put("key", entry.getKey());
	        param.put("value", entry.getValue());
	        dao.insertDocumentVar(param);
	    }

	    return true;
	}

}
