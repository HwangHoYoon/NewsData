package com.news.newsdata.simulation.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SimulationCategoryRes {

    @Schema(description = "'카테고리ID", example = "1", name = "categoryId")
    private Long categoryId;

    @Schema(description = "''카테고리이름", example = "전세사기", name = "catgoryName")
    private String catgoryName;

    @Schema(description = "''카테고리내용", example = "시세보다 저렴한 집, 덜컥 계약해도 괜찮을까요?", name = "categoryContent")
    private String categoryContent;
}
