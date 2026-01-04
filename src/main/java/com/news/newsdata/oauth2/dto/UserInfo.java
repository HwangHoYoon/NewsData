package com.news.newsdata.oauth2.dto;
import com.news.newsdata.oauth2.enums.UserLoginType;

import java.util.Map;

public interface UserInfo {

    UserLoginType getOAuthProvider();

    String getAccessToken();

    Map<String, Object> getAttributes();

    String getId();

    String getEmail();

    String getName();

    String getFirstName();

    String getLastName();

    String getNickname();

    String getThumbnailImageUrl();

    String getProfileImageUrl();
}