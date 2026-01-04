package com.news.newsdata.simulation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "simulation_category")
public class SimulationCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "simulation_category_id_gen")
    @SequenceGenerator(name = "simulation_category_id_gen", sequenceName = "simulation_category_category_id_seq", allocationSize = 1)
    @Column(name = "category_id", nullable = false)
    private Long id;

    @Size(max = 200)
    @NotNull
    @Column(name = "catgory_name", nullable = false, length = 200)
    private String catgoryName;

    @NotNull
    @Column(name = "category_content", nullable = false, length = Integer.MAX_VALUE)
    private String categoryContent;

    @Size(max = 2)
    @NotNull
    @Column(name = "use_yn", nullable = false, length = 2)
    private String useYn;

    @Size(max = 2)
    @NotNull
    @Column(name = "voice_yn", nullable = false, length = 2)
    private String voiceYn;

}