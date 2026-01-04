package com.news.newsdata.simulation.repository;

import com.news.newsdata.simulation.entity.AnalysisRisk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRiskRepository extends JpaRepository<AnalysisRisk, Long> {

    List<AnalysisRisk> findByUser_Id(Long id);
}