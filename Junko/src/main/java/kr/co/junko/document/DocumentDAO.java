package kr.co.junko.document;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ApprovalLineDTO;
import kr.co.junko.dto.ApprovalLogDTO;
import kr.co.junko.dto.DocumentCreateDTO;
import kr.co.junko.dto.DocumentDTO;
import kr.co.junko.dto.MemberDTO;

@Mapper
public interface DocumentDAO {

	int documentInsert(DocumentDTO doc);

	void insertApprovalLine(ApprovalLineDTO line);

	DocumentDTO documentIdx(int document_idx);

	ApprovalLineDTO documentApprove(int document_idx, int user_idx);

	void updateApprove(ApprovalLineDTO line);

	int approveCnt(int document_idx);

	void updateDocStatus(int document_idx, String status);

	String getDocStatus(int document_idx);

	void insertLog(ApprovalLogDTO log);

	int getMinStep(int document_idx);

	List<ApprovalLogDTO> getApprovalLogs(int document_idx);

	List<DocumentDTO> requestedDocument(Map<String, Object> param);
	
	List<DocumentDTO> receivedDocument(Map<String, Object> param);

	int documentCnt(Map<String, Object> param);

	DocumentDTO getByTypeAndIdx(Map<String, Object> map);

	int documentUpdate(DocumentCreateDTO dto);

	void delDocumentVar(int document_idx);

	void insertDocumentVar(Map<String, Object> param);

	List<Map<String, String>> getVariables(int document_idx);

	List<MemberDTO> searchUser(String user_name);

	int currentApprover(int document_idx);

	String approverNames(int document_idx);

	List<ApprovalLineDTO> getApprovalLines(int document_idx);

	int getWriter(int document_idx);

}
