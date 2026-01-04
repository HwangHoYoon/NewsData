package com.news.newsdata.oauth2.handler;

import com.news.newsdata.common.code.MessageCmmCode;
import com.news.newsdata.common.service.JwtProviderService;
import com.news.newsdata.oauth2.dto.OAuth2UserInfoRecord;
import com.news.newsdata.user.response.LoginRes;
import com.news.newsdata.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${home.url}")
    private String homeUrl;

    @Value("${home.error}")
    private String error;

    private final JwtProviderService jwtProviderService;

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2UserInfoRecord userInfoRecord = getOAuth2UserPrincipal(authentication);

        String url = "";
        if (userInfoRecord == null || userInfoRecord.userInfo() == null) {
            url = UriComponentsBuilder.fromUriString(homeUrl+error)
                    .build().toUriString();
        } else {
            LoginRes loginRes = userService.loginGoogle(userInfoRecord.userInfo(), response);
            if (Objects.equals(String.valueOf(loginRes.getCode()), MessageCmmCode.NEW_USER.getCode())) {
                url = UriComponentsBuilder.fromUriString(homeUrl)
                        .queryParam("signup", "true")
                        .queryParam("code", loginRes.getMessage())
                        .build().toUriString();
            } else {
                url = UriComponentsBuilder.fromUriString(homeUrl)
                        .queryParam("code", loginRes.getMessage())
                        .build().toUriString();
            }
        }
        getRedirectStrategy().sendRedirect(request, response, url);
    }

    private OAuth2UserInfoRecord getOAuth2UserPrincipal(Authentication authentication) {
        Object userInfo = authentication.getPrincipal();

        if (userInfo instanceof OAuth2UserInfoRecord) {
            return (OAuth2UserInfoRecord) userInfo;
        }
        return null;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        jwtProviderService.deleteAllToken(request, response);
    }
}
