package com.news.newsdata.simulation.repository;

import com.news.newsdata.simulation.entity.DialogueLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DialogueLogRepository extends JpaRepository<DialogueLog, Long> {
    List<DialogueLog> findBySession_IdOrderByCreatedAtAsc(String id);
}