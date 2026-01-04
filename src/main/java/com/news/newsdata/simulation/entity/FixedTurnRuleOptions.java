package com.news.newsdata.simulation.entity;

import lombok.Data;

import java.util.List;

@Data
public class FixedTurnRuleOptions {
    private List<Double> axes;
    private String text;
    private String verdict;
}
