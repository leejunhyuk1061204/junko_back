package kr.co.junko.document;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ApprovalLineDTO;
import kr.co.junko.dto.ApprovalLogDTO;
import kr.co.junko.dto.DocumentDTO;

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

}
