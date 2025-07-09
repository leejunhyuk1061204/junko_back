package kr.co.junko.adminLog;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect // AOP 를 위한 Aspect 클래스
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminLogAspect {
	
	private final AdminLogService service;
	
	/*
	// 데이터 변경 로그용 지정
	@Pointcut("@annotation(kr.co.junko.adminLog.AdminLogCustom)")
	public void adminOperation() {}
	*/
	
	// 로그 자동 기록 위치
    @Around("execution(* kr.co.junko..*Controller.*(..))")
    public Object saveLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        boolean success = false;

        try {
            result = joinPoint.proceed(); // 실제 메서드 실행
            success = true;
            return result;
        } finally {
        	String methodName = joinPoint.getSignature().getName().toLowerCase(); // 대소문자 무시
        	
        	// 메서드 필터링
            if (!(methodName.contains("insert") || methodName.contains("regist") ||
            		methodName.contains("update") || methodName.contains("delete") || methodName.contains("del"))) {
            	return result;
            }

            // 요청 정보 + 실행 결과를 바탕으로 로그 기록
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String ip_address = req.getRemoteAddr(); // IP 추출
            int admin_idx = getCurrentAdminId(); // JWT에서 id 추출

            String log_type = getLogTypeFromMethodName(methodName);
            String target_table = getTargetTable(joinPoint);
            String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
            String operation_detail = className+"."+methodName+(success ? " success" : " fail");
            
            if (isValidLogType(log_type)) {            					
            	service.saveLog(admin_idx, log_type, target_table, operation_detail, ip_address);
            	log.info("메서드 실행 로그 : {}",operation_detail);
			}
        }
    }
    

	// JWT 에서 id 추출
    private int getCurrentAdminId() {
        return 1; // 테스트용
    }
    
    // 메서드명 기반으로 로그 타입 지정
    private String getLogTypeFromMethodName(String methodName) {	
        if (methodName.contains("insert") || methodName.contains("regist")) return "insert";
        if (methodName.contains("update")) return "update";
        if (methodName.contains("delete") || methodName.contains("del")) return "delete";
        return "etc"; // 기록 안 됨
    }
    
    // enum 에 맞는 값인지 체크
    private boolean isValidLogType(String log_type) {
    	return log_type.equals("insert") || log_type.equals("update") || log_type.equals("delete");
    }

    // 타겟 테이블 이름 유추 (패키지명, 클래스명)
    private String getTargetTable(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        return className.replace("Controller", "").toLowerCase();
    }

}
