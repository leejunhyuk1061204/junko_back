package kr.co.junko.member;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.AdminLogDTO;
import kr.co.junko.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberDAO dao;
	private final JavaMailSender mailSender; // Spring Mail API
	private final Map<String, String> verificationCodes = new ConcurrentHashMap<>(); // 이메일 인증코드 저장용

	public boolean join(MemberDTO dto) {
		int row = dao.join(dto);
		return row>0;
	}

	public boolean overlay(String id) {
		int row = dao.overlay(id);
		return row == 0 ;
	}

	public MemberDTO login(MemberDTO dto, String ip) {
	    MemberDTO user = dao.login(dto); // 로그인 성공한 사용자 정보 가져오기

	    if (user != null) {
	        AdminLogDTO log = new AdminLogDTO();
	        log.setAdmin_idx(user.getUser_idx());
	        log.setLog_type("login");
	        log.setLog_time(LocalDateTime.now());
	        log.setIp_address(ip);

	        dao.insertAdminLog(log); // 로그인 이력 저장
	    }

	    return user; // 성공 여부는 Controller에서 판단
	}

	public boolean logout(String loginId, String ip) {
		int user_idx = dao.getUserIdxById(loginId);
		
		if (user_idx == 0) {
			return false;
		}
		
		AdminLogDTO log = new AdminLogDTO();
		log.setAdmin_idx(user_idx);
		log.setLog_type("logout");
		log.setLog_time(LocalDateTime.now());
		log.setIp_address(ip);
		
		int row = dao.insertAdminLog(log);
		return row>0;
	}

	/*
	 * 클라이언트의 실제 IP 주소를 반환
	 * 프록시(Proxy) 또는 로드밸런서 등을 거친 경우에도 실제 클라이언트 IP를 추출하기 위해 여러 HTTP 헤더를 순차적으로 확인
	 */
	public String getClientIp(HttpServletRequest req) {
		
		// 1. 가장 일반적인 프록시 헤더: X-Forwarded-For (다중 IP 가능, 첫 번째가 원래 클라이언트 IP)
		String ip = req.getHeader("X-Forwarded-For");
		
		// 2. X-Forwarded-For가 없거나 값이 비어있거나 "unknown"이면 다음 헤더 확인
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = req.getHeader("Proxy-Client-IP");
	    }
	    
	    // 3. 위 헤더도 없으면 다음 후보 확인
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = req.getHeader("WL-Proxy-Client-IP");
	    }
	    
	    // 4. 그래도 유효한 IP가 없다면, 직접 요청한 클라이언트의 IP (로컬 서버에서는 127.0.0.1)
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = req.getRemoteAddr();
	    }
	    
	    // 5. 첫 번째 IP만 추출
	    if (ip != null && ip.contains(",")) {
	        ip = ip.split(",")[0];
	    }
	    
	    return ip;
	}

	public String findPw(Map<String, Object> param) {
		return dao.findPw(param);
	}
	
	public boolean resetPw(String user_id, String email) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_id", user_id);
		param.put("email", email);
		String pw = dao.findPw(param);
		return pw != null;
	}
	
	public String sendEmail(String email) {
		String code = createRandomCode(); // 6자리 숫자 생성
		verificationCodes.put(email, code); // 메모리 or Redis (외부 저장소) 에 저장
		
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(email);
		msg.setSubject("비밀번호 재설정 인증코드");
		msg.setText("인증코드: "+code+"\n5분 내에 입력해주세요.");
		mailSender.send(msg);

		return code; // 테스트용 반환 코드
	}
	
	public boolean verifyCode(String email, String code) {
		String correctCode = verificationCodes.get(email);
		log.info("확인용 - correctCode from map : {}", correctCode);
		log.info("검증용 - email: {}, input: {}, correct: {}", email, code, correctCode);
		return code != null && code.equals(correctCode);
	}

	// 랜덤 인증코드 생성 (6자리)
	private String createRandomCode() {
		Random rnd = new Random();
		int num = rnd.nextInt(899999) + 100000; // 100000~999999
		return String.valueOf(num);
	}
	
	public boolean pwUpdate(String user_id, String new_pw) {
		return dao.pwUpdate(user_id, new_pw) > 0;
	}

	public Map<String, Object> userList(Map<String, Object> param) {
		Map<String, Object>result = new HashMap<String, Object>();
		if(param.get("page") != null) {
			int cnt = 10;
			int offset = ((int)param.get("page")-1)*cnt;
			param.put("cnt", cnt);
			param.put("offset", offset);
			int total = dao.userListTotalPage(param);
			result.put("total", total);
		}
		List<MemberDTO>list = dao.userList(param);
		result.put("list", list);
		return result;
	}

	


}
