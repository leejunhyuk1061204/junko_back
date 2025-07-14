package kr.co.junko;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 이거 없으면 프론트에서 이미지 접근 불가
// 스프링 부트에서 외부 디스크에 저장된 파일을 클라이언트에서 보여주려면
// 그 파일의 물리적인 위치와 클라이언트가 요청하는 URL 경로를 매핑해줘야 함
// 그 역할을 하는 클래스가 WebConfig
// 이거 없으면 404 에러 발생
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}") // application.properties에서 읽음
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**") // 예: /images/abc.jpg 로 접근하면
                .addResourceLocations(uploadPath) // file:///C:/upload/abc.jpg 에서 찾아감
                .setCachePeriod(3600); // 1시간 캐싱
    }
}