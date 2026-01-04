package com.news.newsdata.simulation.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SimulationImageRes {
    @Schema(description = "''위험레벨", example = "1", name = "riskLevel")
    private String riskLevel;
    @Schema(description = "제목", example = "1", name = "title")
    private String title;
    @Schema(description = "키워드", example = "1", name = "detectedKeywords")
    private List<String> detectedKeywords;
    @Schema(description = "분석", example = "1", name = "summary")
    private String summary;
    @Schema(description = "가이드", example = "1", name = "guide")
    private String guide;
    @Schema(description = "전체텍스트", example = "1", name = "extractedText")
    private String extractedText;
}
