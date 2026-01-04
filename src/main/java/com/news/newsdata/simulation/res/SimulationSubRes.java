package com.news.newsdata.simulation.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SimulationSubRes {
    @Schema(description = "답변", example = "답변입니다", name = "answer")
    private String answer;
    @Schema(description = "판정값", example = "safe", name = "verdict")
    private String verdict;
}
