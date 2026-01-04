package com.news.newsdata.simulation.res;

import lombok.Data;

import java.time.Instant;

@Data
public class ResultReport {
    private Long id;
    private String title;
    private Instant createdAt;
}
