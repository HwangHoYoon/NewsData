package com.news.newsdata.simulation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "analysis_critical_moment")
public class AnalysisCriticalMoment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analysis_critical_moment_id_gen")
    @SequenceGenerator(name = "analysis_critical_moment_id_gen", sequenceName = "analysis_critical_moment_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "deep_result_id", nullable = false)
    private AnalysisDeepResult deepResult;

    @NotNull
    @Column(name = "turn_number", nullable = false)
    private Integer turnNumber;

    @NotNull
    @Column(name = "user_message", nullable = false, length = Integer.MAX_VALUE)
    private String userMessage;

    @NotNull
    @Column(name = "risk_analysis", nullable = false, length = Integer.MAX_VALUE)
    private String riskAnalysis;

    @NotNull
    @Column(name = "legal_advice", nullable = false, length = Integer.MAX_VALUE)
    private String legalAdvice;

}