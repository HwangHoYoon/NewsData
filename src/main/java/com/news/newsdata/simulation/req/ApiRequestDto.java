package com.news.newsdata.simulation.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequestDto {
    private String user_message;
    private List<DialogueTurn> dialogue_history;
    private UserInfo user_info;
}
