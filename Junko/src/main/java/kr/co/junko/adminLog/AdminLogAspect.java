package kr.co.junko.adminLog;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;

@Aspect // AOP 를 위한 Aspect 클래스
@Component
@RequiredArgsConstructor
public class AdminLogAspect {
	
	private final AdminLogService service;
	
	// 데이터 변경 로그용 지정
	@Pointcut("@annotation(kr.co.junko.adminLog.AdminLogCustom)")
	public void adminOperation() {}
	
	// 로그 자동 기록 위치
    @Around("@annotation(adminLog)")
    public Object logAdminAction(ProceedingJoinPoint joinPoint, AdminLogCustom adminLog) throws Throwable {
        Object result = null;
        boolean success = false;

        try {
            result = joinPoint.proceed(); // 실제 메서드 실행
            success = true;
            return result;
        } finally {
            // 요청 정보 + 실행 결과를 바탕으로 로그 기록
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String ip_address = req.getRemoteAddr(); // IP 추출
            int admin_idx = getCurrentAdminId(); // JWT에서 id 추출

            String log_type = getLogTypeFromMethodName(joinPoint.getSignature().getName());
            String target_table = getTargetTable(joinPoint);
            String operation_detail = adminLog.value() + (success ? " success" : " fail");
            
            if (isValidLogType(log_type)) {            					
            	service.saveLog(admin_idx, log_type, target_table, operation_detail, ip_address);
			}
        }
    }
    

	// JWT 에서 id 추출
    private int getCurrentAdminId() {
        return 1; // 테스트용
    }
    
    // 메서드명 기반으로 로그 타입 지정
    private String getLogTypeFromMethodName(String methodName) {
    	methodName = methodName.toLowerCase(); // 대소문자 무시
    	
        if (methodName.startsWith("insert") || methodName.startsWith("regist")) return "insert";
        if (methodName.startsWith("update")) return "update";
        if (methodName.startsWith("delete") || methodName.startsWith("del")) return "delete";
        return "etc"; // 기록 안 됨
    }
    
    // enum 에 맞는 값인지 체크
    private boolean isValidLogType(String log_type) {
    	return log_type.equals("insert") || log_type.equals("update") || log_type.equals("delete");
    }

    // 타겟 테이블 이름 유추 (패키지명, 클래스명)
    private String getTargetTable(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        return className.replace("Controller", "").replace("Service", "").toLowerCase();
    }


}
