package kr.co.junko;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import kr.co.junko.util.Jwt;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 여기서 Key 초기화
        if (Jwt.getPriKey() == null) {
            Jwt.setPriKey();
        }
        return builder.sources(JunkoApplication.class);
    }
	
//    public static void main(String[] args) {
//        // 내장 톰캣 실행 시에도 Key 초기화
//        if (Jwt.getPriKey() == null) {
//            Jwt.setPriKey();
//        }
//        SpringApplication.run(JunkoApplication.class, args);
//    }

}
