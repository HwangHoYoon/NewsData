package com.news.newsdata.simulation.req;

import lombok.Data;

import java.util.List;

@Data
public class CrimeRequest {
    private String crime_type;
    private String highest_vulnerability_axis;
    private UserInfo user_info;
    private List<DialogueHistory> dialogue_history;

    // getter / setter
}

