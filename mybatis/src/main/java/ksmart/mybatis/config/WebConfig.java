package ksmart.mybatis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ksmart.mybatis.interceptor.CommonInterceptor;
import ksmart.mybatis.interceptor.LoginInterceptor;

//설정에 해당하는 Bean입니다
@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	private final CommonInterceptor commonInterceptor;
	private final LoginInterceptor loginInterceptor;
	public WebConfig(CommonInterceptor commonInterceptor, LoginInterceptor loginInterceptor) {
		this.commonInterceptor = commonInterceptor;
		this.loginInterceptor = loginInterceptor;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {	//인터셉터를 등록하는 registry
		//로깅 인터셉터 등록
		registry.addInterceptor(commonInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/css/**")	//static영역에 해당하는 부분은 매 요청마다 필요하므로 인터셉트 제한에서 뺴줘야한다. 아래 셋!
				.excludePathPatterns("/js/**")	
				.excludePathPatterns("/favicon.ico");
		
		//로그인 인터셉터 등록
		registry.addInterceptor(loginInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/css/**")	
				.excludePathPatterns("/js/**")	
				.excludePathPatterns("/favicon.ico")
				.excludePathPatterns("/")
				.excludePathPatterns("/member/addMember")
				.excludePathPatterns("/member/idCheck")		//로그인 이전 과정에 필요한 Ajax라서(아이디 중복체크) 빠진다. addMember랑 세트지롱
				.excludePathPatterns("/member/login")
				.excludePathPatterns("/member/logout");
		
		WebMvcConfigurer.super.addInterceptors(registry);
	}
}
