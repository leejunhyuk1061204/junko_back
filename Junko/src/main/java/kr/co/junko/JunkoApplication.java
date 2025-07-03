package kr.co.junko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import kr.co.junko.util.Jwt;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableScheduling
public class JunkoApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 외부 톰캣(WAR) 배포 시 Key 초기화
        if (Jwt.getPriKey() == null) {
            Jwt.setPriKey();
        }
        return builder.sources(JunkoApplication.class);
    }

    public static void main(String[] args) {
        // 내장 톰캣(JAR) 실행 시 Key 초기화
        if(Jwt.getPriKey()==null) {
            Jwt.setPriKey();
        }
        SpringApplication.run(JunkoApplication.class, args);
    }
}