package com.news.newsdata.tmp2;

import org.springframework.stereotype.Service;

@Service
public class RuleEngineService {

    public String determineVerdict(String userAction) {
        return switch (userAction) {
            case "대표번호 확인" -> "safe";
            case "사건번호 재확인" -> "risky";
            case "민감정보 제공" -> "unsafe";
            default -> "neutral";
        };
    }

    public AxesDTO determineAxes(String userAction) {
        return switch (userAction) {
            case "대표번호 확인" -> new AxesDTO(0.10, 0.10, 0.00, 0.00);
            case "사건번호 재확인" -> new AxesDTO(0.60, 0.40, 0.10, 0.70);
            case "시간압박 즉답" -> new AxesDTO(0.50, 0.80, 0.10, 0.60);
            case "링크/파일 클릭" -> new AxesDTO(0.50, 0.50, 0.80, 0.70);
            case "민감정보 제공" -> new AxesDTO(0.70, 0.60, 0.30, 0.85);
            default -> new AxesDTO(0.0, 0.0, 0.0, 0.0);
        };
    }
}