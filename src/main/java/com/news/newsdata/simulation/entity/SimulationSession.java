package com.news.newsdata.simulation.entity;

import com.news.newsdata.user.entity.UserInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "simulation_sessions")
public class SimulationSession {
    @Id
    @Column(name = "session_id", nullable = false)
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    @Size(max = 100)
    @NotNull
    @Column(name = "crime_type", nullable = false, length = 100)
    private String crimeType;

    @Size(max = 50)
    @NotNull
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}