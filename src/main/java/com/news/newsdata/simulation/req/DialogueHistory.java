package com.news.newsdata.simulation.req;

import lombok.Data;

import java.util.Map;

@Data
public class DialogueHistory {
    private String role;
    private String text;
    private String verdict;
    private Map<String, Double> axes; // null 가능
    // getter / setter
}
