package com.news.newsdata.simulation.repository;

import com.news.newsdata.simulation.entity.FixedTurnRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedTurnRuleRepository extends JpaRepository<FixedTurnRule, Long> {
  FixedTurnRule findByCrimeTypeAndTurnNumber(String crimeType, Integer turnNumber);
}