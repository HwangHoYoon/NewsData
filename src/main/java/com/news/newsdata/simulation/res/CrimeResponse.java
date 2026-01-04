package com.news.newsdata.simulation.res;

import lombok.Data;

import java.util.List;

@Data
public class CrimeResponse {
    private String next_speech;
    private List<Option> options;

    // getter / setter
}
