package com.news.newsdata.simulation.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

@Data
public class SimulationReportRes {
    @Schema(description = "id", example = "1", name = "id")
    private Long id;
    @Schema(description = "등급", example = "A", name = "grade")
    private String grade;
    @Schema(description = "한줄 총평", example = "한줄", name = "summary")
    private String summary;
    @Schema(description = "주의사항", example = "주의", name = "cautionPoint")
    private String cautionPoint;
    @Schema(description = "한줄 가이드", example = "가이드", name = "guide")
    private String guide;
    @Schema(description = "등록일", example = "20250820", name = "createdAt")
    private Instant createdAt;
}
