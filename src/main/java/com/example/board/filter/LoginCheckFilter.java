package com.example.board.filter;

import java.io.IOException;

import org.springframework.util.PatternMatchUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String requestURI = httpRequest.getRequestURI();

            if (isLoginCheckPath(requestURI)) {
                HttpSession session = httpRequest.getSession(false); // Do not create a new session if one doesn't exist

                if (session == null || session.getAttribute("loggedInUser") == null) {
                    log.info("Request from anonymous user");

                    // Return 401 Unauthorized for unauthenticated users
                    httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "User is not logged in");
                    return;
                }
            }
            chain.doFilter(request, response); // Proceed with the request if the user is authenticated
        } catch (IOException | ServletException e) {
            throw e; // Re-throw exceptions to be handled by the container
        } finally {
            log.info("LoginCheckFilter executed");
        }
    }

    private boolean isLoginCheckPath(String requestURI) {
        String[] whiteList = {"/", "/login", "/register", "/public/*"};
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }
}
