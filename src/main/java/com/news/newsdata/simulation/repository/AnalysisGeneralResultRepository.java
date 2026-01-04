package com.news.newsdata.simulation.repository;

import com.news.newsdata.simulation.entity.AnalysisGeneralResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisGeneralResultRepository extends JpaRepository<AnalysisGeneralResult, Long> {

    List<AnalysisGeneralResult> findByUser_Id(Long id);
}