package com.news.newsdata.simulation.repository;

import com.news.newsdata.simulation.entity.AnalysisDeepResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisDeepResultRepository extends JpaRepository<AnalysisDeepResult, Long> {

    List<AnalysisDeepResult> findByUser_Id(Long id);
}