package com.news.newsdata.simulation.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRiskResponse {
    private String risk_level;
    private String title;
    private List<String> detected_keywords;
    private String summary;
    private String guide;
    private String extracted_text;
}
