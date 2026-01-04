package com.news.newsdata.oauth2.service;

import com.news.newsdata.oauth2.dto.GoogleOAuth2UserInfo;
import com.news.newsdata.oauth2.dto.UserInfo;
import com.news.newsdata.oauth2.enums.UserLoginType;
import com.news.newsdata.oauth2.exception.OAuth2AuthenticationProcessingException;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class OAuth2Factory {

    public static UserInfo getOAuth2UserInfo(String registrationId, String accessToken, Map<String, Object> attributes) {
        if (UserLoginType.GOOGLE.getRegistrationId().equals(registrationId)) {
            return new GoogleOAuth2UserInfo(accessToken, attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("OAuth2Factory Exception : " + registrationId);
        }
    }
}
