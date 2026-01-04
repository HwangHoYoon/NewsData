package com.news.newsdata.tmp2;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HistoryService {

    private final RuleEngineService ruleEngine;
    private final AdaptiveLLMService llmService;
    private final HistoryTurnRepository repository;

    public HistoryService(RuleEngineService ruleEngine, AdaptiveLLMService llmService, HistoryTurnRepository repository) {
        this.ruleEngine = ruleEngine;
        this.llmService = llmService;
        this.repository = repository;
    }

    @Transactional
    public HistoryTurn processTurn(String userText, int turn) {

        HistoryTurnDTO dto;

        if(turn <= 3) { // 고정형
            dto = new HistoryTurnDTO();
            dto.setVerdict(ruleEngine.determineVerdict(userText));
            dto.setAxes(ruleEngine.determineAxes(userText));
            dto.setEvidence("서버 룰 기반 evidence");
        } else { // 적응형
            dto = llmService.analyzeTurn(userText, turn);
            // 서버 보정: 이전 턴 가져오기
            List<HistoryTurn> prevTurns = repository.findAll();
            HistoryTurn lastTurn = prevTurns.isEmpty() ? null : prevTurns.get(prevTurns.size()-1);
            if(lastTurn != null) {
                dto.setAxes(SalienceCalculator.adjustAxes(
                        new AxesDTO(
                                lastTurn.getAxes().getAuthority(),
                                lastTurn.getAxes().getUrgency(),
                                lastTurn.getAxes().getLinkTrust(),
                                lastTurn.getAxes().getNoCallback()
                        ),
                        dto.getAxes()
                ));
            }
        }

        double salience = SalienceCalculator.calculateSalience(dto.getAxes(), 0, dto.getVerdict());

        HistoryTurn entity = new HistoryTurn();
        entity.setTurn(turn);
        entity.setRole("user");
        entity.setText(userText);
        entity.setVerdict(dto.getVerdict());
        Axes axesEntity = new Axes();
        axesEntity.setAuthority(dto.getAxes().getAuthority());
        axesEntity.setUrgency(dto.getAxes().getUrgency());
        axesEntity.setLinkTrust(dto.getAxes().getLinkTrust());
        axesEntity.setNoCallback(dto.getAxes().getNoCallback());
        entity.setAxes(axesEntity);
        entity.setSalience(salience);
        entity.setEvidence(dto.getEvidence());

        return repository.save(entity);
    }
}
