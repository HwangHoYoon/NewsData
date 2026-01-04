package com.news.newsdata.tmp2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryTurnRepository extends JpaRepository<HistoryTurn, Long> { }
