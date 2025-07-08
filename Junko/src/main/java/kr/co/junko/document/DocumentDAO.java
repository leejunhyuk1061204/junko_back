package kr.co.junko.document;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ApprovalLineDTO;
import kr.co.junko.dto.DocumentDTO;

@Mapper
public interface DocumentDAO {

	int documentInsert(DocumentDTO doc);

	void insertApprovalLine(ApprovalLineDTO line);

}
