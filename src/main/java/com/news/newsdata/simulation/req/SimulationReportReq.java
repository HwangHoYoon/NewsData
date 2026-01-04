package com.news.newsdata.simulation.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SimulationReportReq {
    @Schema(description = "세션 ID", example = "1", name = "sessionId")
    private String sessionId;
}
