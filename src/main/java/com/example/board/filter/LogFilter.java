package com.example.board.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class LogFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain)
			// FilterChain 다음 filter 에게 값을 넘김
			throws IOException, ServletException {
		log.info("LogFilter dofilter");
		chain.doFilter(request, response);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("Log filter init");
	}
	
	@Override
	public void destroy() {
		log.info("Log filter destroyed");	
	}
	
}
