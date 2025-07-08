package kr.co.junko.adminLog;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Aspect // AOP 를 위한 Aspect 클래스
@Component
@RequiredArgsConstructor
public class AdminLogAspect {
	
	private final AdminLogService service;
	
	// 데이터 변경 로그용
	@Pointcut("@annotation(kr.co.junko.annota)")
	public void adminOperation() {}
	
	
	

}
