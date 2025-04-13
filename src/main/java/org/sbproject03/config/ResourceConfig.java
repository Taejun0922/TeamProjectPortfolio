package org.sbproject03.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 리소스 파일 환경 설정 클래스
@Configuration
@EnableWebMvc
public class ResourceConfig implements WebMvcConfigurer {
//  application.yml 설정파일에 있는 변수의 값 사용
  @Value("${file.uploadDir}")
  String fileDir;
//  외부에 있는 파일에 대한 접근 경로를 설정
//  /images라는 요청이 올 때, d:/upload/ 폴더에 접근하도록 설정
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/productImages/**", "/images/**", "/icons/**", "/js/**", "/css/**")
        .addResourceLocations("file:///" + fileDir, "classpath:/static/images/", "classpath:/static/icons/", "classpath:/static/js/", "classpath:/static/css/")
        .setCachePeriod(60 * 60 * 24 * 365); // 캐싱 시간
  }
}
