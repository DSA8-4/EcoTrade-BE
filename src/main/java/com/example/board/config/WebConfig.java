package com.example.board.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.board.filter.LogFilter;
import com.example.board.filter.LoginCheckFilter;
import com.example.board.interceptor.LoginCheckInterceptor;
import com.example.board.interceptor.LoginInterceptor;

import jakarta.servlet.Filter;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	private String[] excludePaths = {"/members/register", "/members/login", "/members/logout", "/products/list", "", "/error"};
	
    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                		.allowedOriginPatterns("http://localhost:*","http://127.0.0.1:*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
    

//	@Bean
	FilterRegistrationBean<Filter> logFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
		// 등록할 필터 적용
		filterRegistrationBean.setFilter(new LogFilter());
		
		// 필터의 순서적용, 숫자가 낮을 수록 먼저 실행
		filterRegistrationBean.setOrder(1);
		
		filterRegistrationBean.addUrlPatterns("/*"); // 최상위경로 밑으로 들어오는 모든 경로에 필터를 적용하겠다.
		
		return filterRegistrationBean;
	}
	
//	@Bean
	FilterRegistrationBean<Filter> loginCheckFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
		// 등록할 필터 적용
		filterRegistrationBean.setFilter(new LoginCheckFilter());
		
		// 필터의 순서적용, 숫자가 낮을 수록 먼저 실행
		filterRegistrationBean.setOrder(2);
		
		filterRegistrationBean.addUrlPatterns("/*"); // 최상위경로 밑으로 들어오는 모든 경로에 필터를 적용하겠다.
		
		return filterRegistrationBean;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 인터셉터 등록
		registry.addInterceptor(new LoginInterceptor())
			.order(1) // 인터셉터 호출 순서 지정
			.addPathPatterns("/**") // 인터셉터 적용할 떄 URL 패턴 지정
			.excludePathPatterns(excludePaths); // 인터셉터에서 제외할 패턴
		
		registry.addInterceptor(new LoginCheckInterceptor())
		.order(2) // 인터셉터 호출 순서 지정
		.addPathPatterns("/**") // 인터셉터 적용할 떄 URL 패턴 지정
		.excludePathPatterns(excludePaths); // 인터셉터에서 제외할 패턴
	}
}
