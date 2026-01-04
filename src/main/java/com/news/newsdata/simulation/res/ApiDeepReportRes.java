package com.news.newsdata.simulation.res;

import lombok.Data;

import java.util.List;

@Data
public class ApiDeepReportRes {
    private OverallEvaluation overall_evaluation;
    private List<CriticalMoment> critical_moments;
    private String recommended_action;
}