package com.controller;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CORSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        System.out.println("Request: " + request.getMethod());
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type,Authentication, Accept");
        filterChain.doFilter(request, response);

    }


    @Override
    public void destroy() {

    }
}
