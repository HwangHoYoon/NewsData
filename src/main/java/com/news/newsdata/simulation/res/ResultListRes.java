package com.news.newsdata.simulation.res;

import lombok.Data;

import java.util.List;

@Data
public class ResultListRes {
    private List<ResultReport> reportList;
    private List<ResultReport> deepReportList;
    private List<ResultReport> imageReportList;
}
