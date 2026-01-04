package com.news.newsdata.simulation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "dialogue_logs")
public class DialogueLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dialogue_logs_id_gen")
    @SequenceGenerator(name = "dialogue_logs_id_gen", sequenceName = "dialogue_logs_log_id_seq", allocationSize = 1)
    @Column(name = "log_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private SimulationSession session;

    @Column(name = "turn_number")
    private Integer turnNumber;

    @Size(max = 50)
    @NotNull
    @Column(name = "speaker", nullable = false, length = 50)
    private String speaker;

    @Column(name = "message_text", length = Integer.MAX_VALUE)
    private String messageText;

    @Size(max = 50)
    @Column(name = "\"verdict \"", length = 50)
    private String verdict;

    @Column(name = "axis_authority", precision = 3, scale = 2)
    private BigDecimal axisAuthority;

    @Column(name = "axis_urgency", precision = 3, scale = 2)
    private BigDecimal axisUrgency;

    @Column(name = "axis_link_trust", precision = 3, scale = 2)
    private BigDecimal axisLinkTrust;

    @Column(name = "axis_no_callback", precision = 3, scale = 2)
    private BigDecimal axisNoCallback;

    @Column(name = "salience", precision = 3, scale = 2)
    private BigDecimal salience;

    @Size(max = 255)
    @Column(name = "evidence")
    private String evidence;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}