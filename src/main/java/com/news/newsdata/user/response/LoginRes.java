package com.news.newsdata.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRes {
    private int code;
    private String message;
    private UserLoginRes UserLoginRes;
}
