package com.news.newsdata.simulation.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SimulationVoiceRes {
    @Schema(description = "세션 ID", example = "1", name = "sessionId")
    private String sessionId;

    @Schema(description = "회차", example = "1", name = "turn")
    private int turn;

    @Schema(description = "음성대화", example = "안녕하세요", name = "voiceSpeech")
    private String voiceSpeech;

    @Schema(description = "텍스트대화", example = "안녕하세요", name = "speech")
    private String speech;
}
