package com.news.newsdata.simulation.res;

import lombok.Data;

@Data
public class CriticalMoment {
    private int turn_number;
    private String user_message;
    private String risk_analysis;
    private String legal_advice;
}
