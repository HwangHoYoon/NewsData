package com.news.newsdata.simulation.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SimulationDeepDetailReportRes {
    @Schema(description = "id", example = "1", name = "id")
    private Long id;

    @Schema(description = "회차", example = "1", name = "turnNumber")
    private Integer turnNumber;

    @Schema(description = "유저답변", example = "알겠습니다", name = "userMessage")
    private String userMessage;

    @Schema(description = "위험분석", example = "사기수법입니다", name = "riskAnalysis")
    private String riskAnalysis;

    @Schema(description = "자문", example = "신고하세요", name = "legalAdvice")
    private String legalAdvice;
}
