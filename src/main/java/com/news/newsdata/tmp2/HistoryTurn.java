package com.news.newsdata.tmp2;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "history_turn")
@Data
public class HistoryTurn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int turn;
    private String role;
    private String text;
    private String verdict;

    @Embedded
    private Axes axes;

    private double salience;
    private String evidence;
}

@Embeddable
@Data
class Axes {
    private double authority;
    private double urgency;
    private double linkTrust;
    private double noCallback;
}
