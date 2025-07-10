package kr.co.junko.msg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.tomcat.jni.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.MsgDTO;
import kr.co.junko.file.FileDAO;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class MsgController {

	Map<String, Object> result = null;
	
	@Autowired MsgService service;
	@Autowired FileDAO filedao;

	// 쪽지 보내기
	@PostMapping(value="/msg/send")
	public Map<String, Object> msgSend(@RequestBody MsgDTO dto){
		log.info("dto : {}", dto);
		result = new HashedMap<String, Object>();
		
		boolean success = service.msgSend(dto);
		result.put("success", success);
		
		return result;
	}
	
	// 받은 쪽지
	@GetMapping(value="/msg/received/{user_idx}")
	public Map<String, Object> msgReceived(@PathVariable int user_idx){
	    result = new HashedMap<>();
	    List<MsgDTO> list = service.msgReceived(user_idx);
	    
	    result.put("success", list != null);
	    result.put("list", list);
	    return result;
	}

	// 보낸 쪽지
	@GetMapping(value="/msg/sent/{user_idx}")
	public Map<String, Object> msgSent(@PathVariable int user_idx){
	    result = new HashedMap<>();
	    List<MsgDTO> list = service.msgSent(user_idx);
	    
	    result.put("success", list != null);
	    result.put("list", list);
	    return result;
	}

	
	// 쪽지 삭제하기
	@PutMapping(value="/msg/del/{msg_idx}")
	public Map<String, Object> msgDel(
			@RequestBody MsgDTO dto, 
			@RequestParam String role){
		result = new HashedMap<String, Object>();
		
		boolean success = service.msgDel(dto.getMsg_idx(), dto.getSender_idx(), dto.getReceiver_idx(), role);
		result.put("success", success);
		
		return result;
	}
	
	// 쪽지 읽음
	@PutMapping(value="/msg/read/{msg_idx}")
	public Map<String, Object> msgRead(@PathVariable int msg_idx){
		result = new HashedMap<String, Object>();
		
		boolean success = service.msgRead(msg_idx);
		result.put("success", success);
		
		return result;
	}
	
	// 쪽지 파일 첨부
	@PostMapping(value="/msg/upload")
	public Map<String, Object> msgUpload(
			@RequestParam MultipartFile file,
			@RequestParam int msg_idx){
		result = new HashedMap<String, Object>();
		
		try {
			// 원본 파일명
			String OriName = file.getOriginalFilename();
			// 새 이름 생성
			String uuid = UUID.randomUUID().toString();
			String ext = OriName.substring(OriName.lastIndexOf("."));
			String newName = uuid + ext;
			
			Path uploadPath = Paths.get("C:/upload/msg");
			Files.createDirectories(uploadPath); // 디렉토리 없으면 생성
			
			Path filePath = uploadPath.resolve(newName);
			// 기존 파일 덮어쓰기
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			
			FileDTO dto = new FileDTO();
			dto.setOri_filename(OriName);
			dto.setNew_filename(newName);
			dto.setType("msg");
			dto.setIdx(msg_idx);
			filedao.insertFile(dto);
			
			result.put("success", true);
			result.put("filename", newName);
			
		} catch (IOException e) {
			result.put("success", false);
			result.put("msg", e.getMessage());
		} 
		
		return result;
	}
	
}
