package com.news.newsdata.simulation.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SimulationVoiceReq {
    @Schema(description = "세션 ID", example = "1", name = "sessionId")
    private String sessionId;

    @Schema(description = "회차", example = "1", name = "turn")
    private int turn;

    @Schema(description = "답변", example = "답변입니다", name = "answer")
    private String answer;
}
