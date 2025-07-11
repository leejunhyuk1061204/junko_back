package kr.co.junko.purchaseSettlement;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.PurchaseSettlementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchasesettlementService {

	
	@Autowired
	private final PurchasesettlementDAO dao;
	
	 private final String dir = "C:/upload";
	Map<String, Object> result = null;
	
	public PurchaseSettlementDTO getSettlementById(int settlement_idx) {
		return dao.getSettlementById(settlement_idx);
	}
	
	public Object psRegister(PurchaseSettlementDTO dto) {
		return dao.psRegister(dto);
		
	}
	
	public int settlementUpdate(PurchaseSettlementDTO dto) {
		return dao.settlementUpdate(dto);
	}
	
	public int settlementDel(int settlement_idx) {
		return dao.settlementDel(settlement_idx);
	}
	
	public int settlementFinal(int settlement_idx) {
		return dao.settlementFinal(settlement_idx);
	}
	
	public boolean settlementReq(int settlement_idx) {
		
		PurchaseSettlementDTO dto = dao.getSettlementById(settlement_idx);
	    if (dto == null) return false;

	    // 확정 또는 마감 상태에서만 요청 가능
	    if ("확정".equals(dto.getStatus()) || "마감".equals(dto.getStatus())) {
	        return dao.settlementReq(settlement_idx) > 0;
	    }

	    return false;
	}

	public boolean settlementAdminReq(int settlement_idx) {
		 PurchaseSettlementDTO dto = dao.getSettlementById(settlement_idx);
		    if (dto == null) return false;

		    if ("재정산요청".equals(dto.getStatus())) {
		        return dao.settlementAdminReq(settlement_idx) > 0;
		    }

		    return false;
	}

	public List<FileDTO> settlementFileUpload(int settlement_idx, MultipartFile[] files, String type) throws IllegalStateException, IOException {
		List<FileDTO> savedFiles = new ArrayList();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String ori = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String newName = uuid + "/" + ori;
            String savePath = dir + newName;

            // 실제 파일 저장
            File dest = new File(savePath);
            dest.getParentFile().mkdirs();
            file.transferTo(dest);

            // DTO 만들고 DB 저장
            FileDTO dto = new FileDTO();
            dto.setOri_filename(ori);
            dto.setNew_filename(newName);
            dto.setReg_date(LocalDateTime.now());
            dto.setType(type != null ? type : "기타");
            dto.setIdx(settlement_idx);
            dto.setDel_yn(false);

            dao.settlementFileUpload(dto);
            savedFiles.add(dto);
        }

        return savedFiles;
    }

	public List<FileDTO> settlementFileList(int settlement_idx) {
		return dao.settlementFileList(settlement_idx);
	}

	public int settlementFileDel(int idx) {
		
		return dao.settlementFileDel(idx);
	}

	public FileDTO settlementFileDown(int idx) {
		
		return dao.settlementFileDown(idx);
	}
	
	
}
