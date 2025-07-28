package kr.co.junko.msg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.MsgDTO;
import kr.co.junko.file.FileDAO;
import kr.co.junko.util.Jwt;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class MsgController {

	Map<String, Object> result = null;
	
	@Autowired MsgService service;
	@Autowired FileDAO filedao;

	@Value("${spring.servlet.multipart.location}") 
	private String root;
	
	// 쪽지 보내기
	@PostMapping(value="/msg/send")
	public Map<String, Object> msgSend(@RequestBody MsgDTO dto){
		log.info("dto : {}", dto);
		result = new HashedMap<String, Object>();
		
		int cnt = 0;
		for (int receiver_idx : dto.getReceiver_list()) {
			dto.setReceiver_idx(receiver_idx);
			boolean success = service.msgSend(dto);
			if (success) {
				cnt++;
			}
		}
		
		result.put("success", cnt == dto.getReceiver_list().size());
		result.put("sent_cnt", cnt);
		
		return result;
	}
	
	// 받은 쪽지
	@GetMapping(value="/msg/received/{user_idx}")
	public Map<String, Object> msgReceived(@PathVariable int user_idx){
	    result = new HashedMap<>();
	    
	    service.oldMsgDel(); // 받은 쪽지 매번 볼 때마다 검사
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
			
			Path uploadPath = Paths.get(root);
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
	
	// 수신인 자동 완성
	@GetMapping(value="/msg/auto")
	public Map<String, Object> userAuto(@RequestParam String keyword){
		result = new HashedMap<String, Object>();
		
		List<Map<String, Object>> list = service.userAuto(keyword);
		result.put("list", list);
		
		return result;
	}
	
	// 쪽지 중요 표시
	@PutMapping(value="/msg/important/{msg_idx}")
	public Map<String, Object>msgImportant(
			@PathVariable int msg_idx,
			@RequestParam int user_idx,
			@RequestParam boolean important_yn){
		result = new HashedMap<String, Object>();
		
		boolean success = service.msgImportant(msg_idx, user_idx, important_yn);
		result.put("success", success);
		
		return result;
	}
	
	// 중요 쪽지 조회
	@GetMapping(value="/msg/important/view/{user_idx}")
	public Map<String, Object> msgImportantView(@PathVariable int user_idx){
		result = new HashedMap<String, Object>();
		
		List<MsgDTO> list = service.msgImportantView(user_idx);
		result.put("list", list);
		
		return result;
	}
	
	// 안 읽은 쪽지
	@GetMapping("/msg/unread/cnt/{user_idx}")
	public Map<String, Object> msgUnreadCnt(@PathVariable int user_idx){
		result = new HashedMap<String, Object>();
		
		int cnt = service.msgUnreadCnt(user_idx);
		result.put("cnt", cnt);
		
		return result;
	}
	
	@PostMapping(value="/msg/list")
	public Map<String, Object>msgList(@RequestBody Map<String, Object>param){
		log.info("param : {}", param);
		return service.msgList(param);
	}
	
	@PostMapping(value="/msg/insert")
	public Map<String, Object>msgInsert(@RequestBody MsgDTO dto, @RequestHeader Map<String, String>header){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		String token = header.get("authorization");
		Map<String, Object>payload = Jwt.readToken(token);
		String loginId = (String)payload.get("user_id");
		boolean login = loginId != null && !loginId.isEmpty();
		boolean success = false;
		if(login) {
			success = service.msgInsert(dto);
		}
		result.put("success", success);
		return result;
	}
	
}
