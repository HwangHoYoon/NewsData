package com.news.newsdata.simulation.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class SimulationDeepReportRes {

    @Schema(description = "등급", example = "A", name = "grade")
    private String grade;

    @Schema(description = "종합소견", example = "종합", name = "summary")
    private String summary;

    @Schema(description = "권장조치", example = "권장", name = "recommendedAction")
    private String recommendedAction;

    @Schema(description = "등록일", example = "20250820", name = "createdAt")
    private Instant createdAt;

    @Schema(description = "상세목록", example = "1", name = "detailLst")
    private List<SimulationDeepDetailReportRes> detailList;
}
