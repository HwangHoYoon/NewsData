package com.news.newsdata.simulation.repository;

import com.news.newsdata.simulation.entity.SimulationSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationSessionRepository extends JpaRepository<SimulationSession, String> {
}