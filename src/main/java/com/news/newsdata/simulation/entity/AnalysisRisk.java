package com.news.newsdata.simulation.entity;

import com.news.newsdata.user.entity.UserInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "analysis_risk")
public class AnalysisRisk {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analysis_risk_id_gen")
    @SequenceGenerator(name = "analysis_risk_id_gen", sequenceName = "analysis_risk_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "risk_level", nullable = false, length = 50)
    private String riskLevel;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "detected_keywords", length = Integer.MAX_VALUE)
    private String detectedKeywords;

    @Column(name = "summary", length = Integer.MAX_VALUE)
    private String summary;

    @Column(name = "guide", length = Integer.MAX_VALUE)
    private String guide;

    @Column(name = "extracted_text", length = Integer.MAX_VALUE)
    private String extractedText;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

}