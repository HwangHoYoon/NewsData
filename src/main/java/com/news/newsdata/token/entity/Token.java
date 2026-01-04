package com.news.newsdata.token.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Schema(description = "Token VO")
@NoArgsConstructor
public class Token {
    @Id
    private String userId;
    private String accessToken;
    private String refreshToken;

    public Token(String refreshToken, String userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

    public Token(String refreshToken, String userId, String accessToken) {
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.accessToken = accessToken;
    }

    public Token updateToken(String token) {
        this.refreshToken = token;
        return this;
    }
}
