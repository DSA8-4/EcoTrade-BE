package com.example.board.interceptor;

import java.util.Enumeration;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
//		String requestURI = request.getRequestURI();
//		HttpSession session = request.getSession(false);
//		
//		if(session == null || session.getAttribute("loginMember") == null) {
//			log.info("Not logged in User");
//			
//			Enumeration<String> parameterNames = request.getParameterNames();
//			
//			StringBuffer stringBuffer = new StringBuffer();
//			
//			while(parameterNames.hasMoreElements()) {
//				String parameterName = parameterNames.nextElement();
//				stringBuffer.append(parameterName + "=" + request.getParameter(parameterName) + "%26");
//			}
////			response.sendRedirect("/member/login?redirectURL=" + requestURI);
//			response.sendRedirect("/member/login?redirectURL=" + requestURI + "?" + stringBuffer.toString());
//			return false;
//		}
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
}
