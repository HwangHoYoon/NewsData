package com.news.newsdata.simulation.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SimulationDeepReportReq {
    @Schema(description = "리포트ID", example = "1", name = "reportId")
    private String reportId;
}
