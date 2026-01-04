package com.news.newsdata.simulation.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SimulationRes {
    @Schema(description = "세션 ID", example = "1", name = "sessionId")
    private String sessionId;

    @Schema(description = "회차", example = "1", name = "turn")
    private int turn;

    @Schema(description = "대화", example = "안녕하세요", name = "speech")
    private String speech;

    @Schema(description = "답변 목록", example = "답변입니다", name = "answers")
    private List<SimulationSubRes> answers;

    @Schema(description = "Y", example = "대화 마지막 여부", name = "lastYn")
    private boolean lastYn;
}
