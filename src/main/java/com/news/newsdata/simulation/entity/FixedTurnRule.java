package com.news.newsdata.simulation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "fixed_turn_rules")
public class FixedTurnRule {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fixed_turn_rules_id_gen")
    @SequenceGenerator(name = "fixed_turn_rules_id_gen", sequenceName = "fixed_turn_rules_rule_id_seq", allocationSize = 1)
    @Column(name = "rule_id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "crime_type", nullable = false, length = 100)
    private String crimeType;

    @Column(name = "turn_number")
    private Integer turnNumber;

    @Column(name = "ai_speech", length = Integer.MAX_VALUE)
    private String aiSpeech;

    @NotNull
    @Column(name = "options_json", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<FixedTurnRuleOptions> options;

}