package com.news.newsdata.tmp2;

import org.springframework.stereotype.Service;

@Service
public class AdaptiveLLMService {

    public HistoryTurnDTO analyzeTurn(String userText, int turn) {
        // 실제로는 LLM API 호출 후 JSON 파싱
        // 예제용 더미 값
        HistoryTurnDTO dto = new HistoryTurnDTO();
        dto.setVerdict("unsafe");
        dto.setAxes(new AxesDTO(0.72,0.58,0.20,0.88));
        dto.setEvidence("역조회 없이 신원 정보 제공 의사 표명");
        return dto;
    }
}
