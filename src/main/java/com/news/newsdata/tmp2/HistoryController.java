package com.news.newsdata.tmp2;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/turn")
    public HistoryTurn submitTurn(@RequestParam String text, @RequestParam int turn) {
        return historyService.processTurn(text, turn);
    }
}
