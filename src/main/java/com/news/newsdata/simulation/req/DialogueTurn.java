package com.news.newsdata.simulation.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DialogueTurn {
    private String role;
    private String text;
}
