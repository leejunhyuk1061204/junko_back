package kr.co.junko.member;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.MemberDTO;
import kr.co.junko.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController {

	private final MemberService service;
	Map<String, Object> result = null;
	
	// 회원가입
	@PostMapping(value="/join")
	public Map<String, Object>join (@RequestBody MemberDTO dto){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		boolean success = service.join(dto);
		result.put("success", success);
		return result;
	}
	
	// 아이디 중복체크
	@GetMapping(value="/overlay/{user_id}")
	public Map<String, Object> overlay(@PathVariable String user_id){
		log.info("id"+user_id);
		result= new HashMap<String, Object>();
		boolean success = service.overlay(user_id);
		result.put("success", success);
		return result;
	}
	
	// 로그인 (+로그 저장)
	@PostMapping("/login")
	public Map<String, Object> login(@RequestBody MemberDTO dto, HttpServletRequest req){
		log.info("dto : {}", dto);
		result = new HashMap<String, Object>();
		String loginId = dto.getUser_id();
		boolean success = false;
		boolean login = false;
		
		if(loginId != null && !loginId.isEmpty()) {
			String ip = service.getClientIp(req); // IP 정보
			success = service.login(dto, loginId, ip);
			
			if (success) {
				String token = Jwt.setToken("user_id",loginId);
				result.put("token", token);
				login = true;
			}
			result.put("success", success);
			result.put("login", login);
		}
		return result;
	}
	
	// 로그아웃 (+로그 저장)
	@PostMapping("/logout")
	public Map<String, Object> logout(HttpServletRequest req, @RequestHeader Map<String, String> header) {
		result = new HashMap<String, Object>();
		boolean success = false;
		
		String token = header.get("authorization");
		if (token != null && !token.isEmpty()) {
			String loginId = (String) Jwt.readToken(token).get("user_id");
			String ip = service.getClientIp(req);
			
			if (loginId != null) {
				success = service.logout(loginId, ip);
			}
		}
		result.put("success", success);
		return result;
	}
	
	// 비밀번호 찾기
	@PostMapping("/find/pw")
	public Map<String, Object> findPw(@RequestBody Map<String, Object> param) {
		log.info("find pw : {}", param);
		result = new HashMap<String, Object>();
		String userPw = service.findPw(param);
		result.put("success", true);
		result.put("pw", userPw);
		return result;
	}
	
	// 이메일 인증코드 전송
	@PostMapping("/sendEmail")
	public Map<String, Object> sendEmail(@RequestBody Map<String, Object> param) {
		log.info("send email : {}", param);
		result = new HashMap<String, Object>();
		String user_id = (String) param.get("user_id");
		String email = (String) param.get("email");
		
		boolean success = service.resetPw(user_id, email);
		if (success) {
			String code = service.sendEmail(email);
			result.put("success", success);
		}
		return result;
	}

	// 인증코드 확인
	@PostMapping("/verifyCode")
	public Map<String, Object> verifyCode(@RequestBody Map<String, Object> param) {
		log.info("code : {}", param);
		result = new HashMap<String, Object>();
		String email = (String) param.get("email");
		String code = (String) param.get("code");

		boolean success = service.verifyCode(email, code);
		result.put("success", success);
		return result;
	}

	// 비밀번호 변경
	@PostMapping("pw/update")
	public Map<String, Object> pwUpdate(@RequestBody Map<String, Object> param) {
		log.info("reset pw : {}", param);
		result = new HashMap<String, Object>();
		String user_id = (String) param.get("user_id");
		String new_pw = (String) param.get("new_pw");

		boolean success = service.pwUpdate(user_id, new_pw);
		result.put("success", success);
		return result;
	}

}
