package com.news.newsdata.user.entity;

import lombok.Builder;

import java.util.Date;

public record UserInfoEditor(String name, Date updDate) {

    @Builder
    public UserInfoEditor {
    }
}
