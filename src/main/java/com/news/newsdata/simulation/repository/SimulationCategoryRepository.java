package com.news.newsdata.simulation.repository;

import com.news.newsdata.simulation.entity.SimulationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimulationCategoryRepository extends JpaRepository<SimulationCategory, Long> {
  List<SimulationCategory> findByVoiceYnAndUseYnOrderByIdAsc(String voiceYn, String useYn);
}