package com.news.newsdata.token.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.news.newsdata.common.code.MessageCmmCode;
import com.news.newsdata.common.exception.ExceptionCode;
import com.news.newsdata.common.service.JwtProviderService;
import com.news.newsdata.token.dto.TokenDto;
import com.news.newsdata.token.entity.Token;
import com.news.newsdata.token.request.TokenReq;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenUserService {
    private final JwtProviderService jwtProviderService;
    private final TokenService tokenService;
    public ResponseEntity<?> refreshToken(String refreshToken, TokenReq tokenReq, HttpServletResponse response) {
        // 리프레시 토큰 검증
        if (Boolean.TRUE.equals(jwtProviderService.tokenValidation(refreshToken))) {
            // redis에 있는지 확인
            Token token = tokenService.findById(refreshToken);
            if (token == null) {
                log.error("Refresh token not found in Redis: {}", refreshToken);
                jwtExceptionHandler(response, HttpStatus.UNAUTHORIZED);
            } else {
                String userId = tokenReq.getUserId();
                if (StringUtils.isBlank(userId)) {
                    log.error("User ID is blank in token request: {}", tokenReq);
                    jwtExceptionHandler(response, HttpStatus.UNAUTHORIZED);
                } else {
                    String tokenUserId = token.getUserId();
                    if (!userId.equals(tokenUserId)) {
                        log.error("User ID mismatch: request user ID = {}, token user ID = {}", userId, tokenUserId);
                        jwtExceptionHandler(response, HttpStatus.UNAUTHORIZED);
                    } else {
                        // 인증 완료 새로운 토큰 발행
                        TokenDto tokenDto = jwtProviderService.createAllToken(userId);
                        // response 헤더에 Access Token / Refresh Token 넣음
                        jwtProviderService.setResponseNmtoken(response, tokenDto);
                    }
                }
            }
        } else {
            log.error("Invalid refresh token: {}", refreshToken);
            jwtExceptionHandler(response, HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(MessageCmmCode.OK.getMessage());
    }

    public void jwtExceptionHandler(HttpServletResponse response, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", ExceptionCode.TOKEN_NULL.getCode());
            responseBody.put("message", ExceptionCode.TOKEN_NULL.getMessage());
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public ResponseEntity<?> token(String code, HttpServletResponse response) {
        Token token = tokenService.findByHeaderId(code);
        TokenDto tokenDto = new TokenDto(token.getAccessToken(), token.getRefreshToken(), token.getUserId());
        jwtProviderService.setResponseNmtoken(response, tokenDto);
        return ResponseEntity.ok(MessageCmmCode.OK.getMessage());
    }
}
