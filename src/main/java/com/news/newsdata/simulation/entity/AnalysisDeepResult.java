package com.news.newsdata.simulation.entity;

import com.news.newsdata.user.entity.UserInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "analysis_deep_result")
public class AnalysisDeepResult {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analysis_deep_result_id_gen")
    @SequenceGenerator(name = "analysis_deep_result_id_gen", sequenceName = "analysis_deep_result_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 5)
    @NotNull
    @Column(name = "grade", nullable = false, length = 5)
    private String grade;

    @NotNull
    @Column(name = "summary", nullable = false, length = Integer.MAX_VALUE)
    private String summary;

    @Column(name = "recommended_action", length = Integer.MAX_VALUE)
    private String recommendedAction;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private SimulationSession session;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private AnalysisGeneralResult report;

    @OneToMany(mappedBy = "deepResult")
    private Set<AnalysisCriticalMoment> analysisCriticalMoments = new LinkedHashSet<>();

}