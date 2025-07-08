package kr.co.junko.collectonAndPayment;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID; 

import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.CollectionAndPaymentLogDTO;
import kr.co.junko.dto.CollectionAndPaymentRequestDTO;
import kr.co.junko.dto.CollectionAndPaymentResponseDTO;
import kr.co.junko.dto.CustomDTO;
import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.LinkedItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectionAndPaymentService {

	@Autowired
	private final CollectionAndPaymentDAO dao;
	private final String dir = "c:/upload";
	Map<String, Object> result = null;
	
	public int capRegist(CollectionAndPaymentRequestDTO dto) {
		boolean exites = dao.capRegist(
				dto.getType(),dto.getDate(),dto.getAmount(),dto.getCustom_idx()
				);
		
	if (exites) {
		throw new IllegalArgumentException("동일한 거래 내역이 이미 존재합니다.");
	}
				
		dao.insert(dto);
		return dto.getCap_idx();
		
	}

	public CollectionAndPaymentResponseDTO capList(int cap_idx) {
		return dao.capList(cap_idx);
	}

	public boolean capUpdate(CollectionAndPaymentRequestDTO dto) {
		    boolean exists = dao.capRegist(dto.getType(), dto.getDate(), dto.getAmount(), dto.getCustom_idx());

		    if (exists) return false;

		    dao.capUpdate(dto);
		    return true;
		}

	public boolean capDel(int cap_idx) {
		return dao.capDel(cap_idx) > 0;
	}

	

	public List<CustomDTO> capCustom() {
		 return dao.capCustom();
	}

	public List<CustomDTO> capCustomList() {
		return dao.capCustomList();
	}

	public LinkedItemDTO getEntryList() {
		return dao.getEntryList();
	}

	public LinkedItemDTO getSettlementList() {
		return dao.getSettlementList();
	}

	public LinkedItemDTO getInvoiceList() {
		return dao.getInvoiceList();
	}

	public Map<String, Object> capFile(String string, int cap_idx, MultipartFile file) {
		result = new HashMap<String, Object>();
		
		try {
            String oriName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String ext = oriName.substring(oriName.lastIndexOf("."));
            String newName = uuid + ext;

            Path filePath = Paths.get(dir + newName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());

            FileDTO dto = new FileDTO();
            dto.setOri_filename(oriName);
            dto.setNew_filename(newName);
            dto.setType("collection");
            dto.setIdx(cap_idx);
            dto.setDel_yn(false);
            dto.setReg_date(LocalDateTime.now());

            dao.insertFile(dto);

            result.put("success", true);
            result.put("message", "업로드 성공");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "업로드 실패: " + e.getMessage());
        }

        return result;
	}

	public ResponseEntity<FileSystemResource> capDown(int file_idx) {
		 FileDTO file = dao.getFileByIdx(file_idx);
	        if (file == null) {
	            return ResponseEntity.notFound().build();
	        }

	        Path path = Paths.get(dir + file.getNew_filename());
	        FileSystemResource resource = new FileSystemResource(path.toFile());

	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION,
	                        "attachment; filename=\"" + file.getOri_filename() + "\"")
	                .body(resource);
	}

	public Map<String, Object> capFileDel(int file_idx) {
		result = new HashMap<String, Object>();
		
		 try {
	            dao.deleteFile(file_idx);
	            result.put("success", true);
	            result.put("message", "삭제 완료");
	        } catch (Exception e) {
	            result.put("success", false);
	            result.put("message", "삭제 실패: " + e.getMessage());
	        }

	        return result;
	}

	public List<CollectionAndPaymentLogDTO> getLogsByCapIdx(int cap_idx) {
		return dao.getLogsByCapIdx(cap_idx);
	}


	
}
