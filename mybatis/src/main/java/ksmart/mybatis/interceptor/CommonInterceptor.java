package ksmart.mybatis.interceptor;

import java.util.Set;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 컴포넌트라고 붙은 객체들은 Bean으로 관리됨. bean의 이름은 CommonInterceptor
// 인터셉터는 잘 구현되어 있어서 구현체로 쓰면 됨
@Component
public class CommonInterceptor implements HandlerInterceptor{
	
	
	private static final Logger log = LoggerFactory.getLogger(CommonInterceptor.class);

	
	//핸들러는 컨트롤러라고 생각
	/**
	 * HandlerMapping이 핸들러 객체를 결정한 후 HandlerAdapter가 핸들러를 호출하기 전에 호출되는 메소드
	 * return true(핸들러메소드 실행), false(핸들러 메소드 실행x: 핸들러까지 진입 금지)
	 * */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {	//컨트롤러 전이라서 ModelAndView modelAndView 없음
		
		//파라미터 값을 확인
		Set<String> paramKeySet = request.getParameterMap().keySet();	 //키들만 자료구조에 넣어서 관리한다
		
		StringJoiner param = new StringJoiner(", ");	//결과 내용을 구분자를 포함한 문자열로 만들어준다
		
		//ex) memberId: id001, memberPw: pw001
		for(String key : paramKeySet) {
			param.add(key + ": " + request.getParameter(key));
		}
		
		log.info("ACESS INFO===================================");
		log.info("PORT      		:::::::::         {}", request.getLocalPort());
		log.info("ServerName      	:::::::::         {}", request.getServerName ());
		log.info("HTTPMethod      	:::::::::         {}", request.getMethod());
		log.info("URI      		:::::::::         {}", request.getRequestURI());
		log.info("CLIENT IP      	:::::::::         {}", request.getRemoteAddr());
		log.info("Parameter      	:::::::::         {}", param);
		log.info("ACESS INFO===================================");
		
		return HandlerInterceptor.super.preHandle(request, response, handler);	//기본값이 boolean 형태이다. true~
	}
	
	
	/**
	 * HandlerAdapter가 실제로 핸들러를 호출한 후 DispatcherServlet이 뷰를 전달되기 전에 호출되는 메소드
	 * */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {	//ModelAndView modelAndView 뷰 전달 전이라서 있음
															//Object handler가 바인딩 되었을수도 없을수도. 요청이 너무 빨리 일어나면 걍 넘어가버리기떄문
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	
	/**
	 * DispatcherServlet이 뷰에 전달된 후 뷰를 렌더링하기 전에 호출되는 메소드
	 * */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {		//뷰에 해당하는 렌더링이 끝나서 ModelAndView modelAndView 없음
			
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

}
