package com.news.newsdata.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.news.newsdata.common.exception.ExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        setResponse(response, ExceptionCode.SECURITY.getMessage());
    }
    private void setResponse(HttpServletResponse response, String message) throws IOException {
        log.error("[exceptionHandle] AuthenticationEntryPoint = {}", message);
        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", 403);
        responseBody.put("error", "Forbidden");
        responseBody.put("message", "권한이 없습니다.");

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}