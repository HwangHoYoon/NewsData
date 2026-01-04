package com.news.newsdata.token.controller;

import com.news.newsdata.common.service.JwtProviderService;
import com.news.newsdata.token.request.TokenReq;
import com.news.newsdata.token.service.TokenUserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Token", description = "토큰 API")
@RequestMapping("token")
public class TokenController {
    private final TokenUserService tokenUserService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestBody TokenReq tokenReq,
            @RequestHeader(JwtProviderService.AUTHORIZATION) String refreshToken,
        @Parameter(hidden = true) HttpServletResponse response
    ) {
        return tokenUserService.refreshToken(refreshToken, tokenReq, response);
    }

    @GetMapping("/token")
    public ResponseEntity<?> token(
            @Parameter(hidden = true) HttpServletResponse response,
            @Schema(description = "code", example = "1", name = "code") @RequestParam(name = "code") String code
    ) {
        return tokenUserService.token(code, response);
    }
}
