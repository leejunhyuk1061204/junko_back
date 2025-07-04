package kr.co.junko.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.MemberDTO;
import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController {

	private final MemberService service;
	Map<String, Object> result = null;
	
	@PostMapping(value="/join")
	public Map<String, Object>join (@RequestBody MemberDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.join(dto);
		result.put("success", success);
		return result;
	}
	
	@GetMapping(value="/overlay/{user_id}")
	public Map<String, Object> overlay(@PathVariable String user_id){
		log.info("id"+user_id);
		result= new HashMap<String, Object>();
		boolean success = service.overlay(user_id);
		result.put("success", success);
		return result;
	}
	
	@PostMapping(value="/login")
	public Map<String, Object>login(@RequestBody MemberDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.login(dto);
		String loginId = dto.getUser_id();
		boolean login = false;
		if(!loginId.equals("") && loginId != null && success) {
			String token = Jwt.setToken("user_id",loginId);
			result.put("token", token);
			login = true;
		}
		result.put("login", login);
		return result;
	}
	
}
