package com.controller;

import com.entity.Credential;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String method = request.getMethod();
        switch (method) {
            case "GET":
                filterChain.doFilter(servletRequest, servletResponse);
                break;
            case "POST":
                filterChain.doFilter(servletRequest, servletResponse);
                break;
            case "PUT":
            case "DELETE":
                String authenticationHeader = request.getHeader("Authentication");
                Credential credential = ofy().load().type(Credential.class).filter("tokenKey", authenticationHeader).first().now();
                if (credential != null) {
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    servletResponse.getWriter().write("ACCOUNT IS UNAUTHORIZE, PLEASE CONTACT ADMIN");
                    ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
                }

                break;
        }
    }

    @Override
    public void destroy() {

    }
}
