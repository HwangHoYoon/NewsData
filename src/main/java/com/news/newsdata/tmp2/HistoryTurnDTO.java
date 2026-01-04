package com.news.newsdata.tmp2;

import lombok.Data;

@Data
public class HistoryTurnDTO {
    private int turn;
    private String role; // "user" | "agent"
    private String text;
    private String verdict;
    private AxesDTO axes;
    private double salience;
    private String evidence;
}
