package com.news.newsdata.simulation.service;

import com.news.newsdata.api.google.tts.TextToSpeechService;
import com.news.newsdata.common.dto.UserDetailsImpl;
import com.news.newsdata.simulation.entity.*;
import com.news.newsdata.simulation.repository.*;
import com.news.newsdata.simulation.req.*;
import com.news.newsdata.simulation.res.*;
import com.news.newsdata.user.entity.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class SimulationService {

    private final RestTemplate restTemplate;

    private final FixedTurnRuleRepository fixedTurnRuleRepository;

    private final SimulationSessionRepository simulationSessionRepository;

    private final DialogueLogRepository dialogueLogRepository;

    private final TextToSpeechService textToSpeechService;

    private final AnalysisGeneralResultRepository analysisGeneralResultRepository;

    private final AnalysisDeepResultRepository analysisDeepResultRepository;

    private final AnalysisCriticalMomentRepository analysisCriticalMomentRepository;

    private final AnalysisRiskRepository analysisRiskRepository;

    private final SimulationCategoryRepository simulationCategoryRepository;

    public SimulationRes firstTurn(String type) {
        SimulationRes simulationRes = new SimulationRes();
        // 타입에 맞는 첫번째 고정 질문 가져옴
        String uuid = UUID.randomUUID().toString();
        List<SimulationSubRes> answers = new ArrayList<>();
        FixedTurnRule fixedTurnRule = fixedTurnRuleRepository.findByCrimeTypeAndTurnNumber(type, 1);
        if (fixedTurnRule != null) {
            simulationRes.setTurn(fixedTurnRule.getTurnNumber());
            simulationRes.setSpeech(fixedTurnRule.getAiSpeech());
            List<FixedTurnRuleOptions> optionList = fixedTurnRule.getOptions();
            for (FixedTurnRuleOptions option : optionList) {
                SimulationSubRes simulationSubRes = new SimulationSubRes();
                simulationSubRes.setAnswer(option.getText());
                simulationSubRes.setVerdict(option.getVerdict());
                answers.add(simulationSubRes);
            }
            simulationRes.setAnswers(answers);
            simulationRes.setSessionId(uuid);
        }

        // 시뮬레이션 Session 생성
        SimulationSession simulationSession = new SimulationSession();
        simulationSession.setId(uuid);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            simulationSession.setUser(userDetails.getUser());
        }
        simulationSession.setStatus("IN_PROGRESS");
        simulationSession.setCrimeType(type);
        simulationSession.setCreatedAt(Instant.now());
        simulationSessionRepository.save(simulationSession);

        // 대화 저장
        DialogueLog dialogueLog = new DialogueLog();
        dialogueLog.setSession(simulationSession);
        dialogueLog.setSpeaker("agent");
        dialogueLog.setMessageText(Objects.requireNonNull(fixedTurnRule).getAiSpeech());
        dialogueLog.setTurnNumber(Objects.requireNonNull(fixedTurnRule).getTurnNumber());
        dialogueLog.setCreatedAt(Instant.now());
        dialogueLogRepository.save(dialogueLog);

        return simulationRes;
    }

    public SimulationRes turn(SimulationReq simulationReq) {
        SimulationRes simulationRes = new SimulationRes();
        String sessionId = simulationReq.getSessionId();
        int turn = simulationReq.getTurn();
        String answer = simulationReq.getAnswer();
        String verdict = simulationReq.getVerdict();

        // 세션 정보에서 타입 가져오기
        SimulationSession simulationSession = simulationSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        String type = simulationSession.getCrimeType();

        List<Double> axes = null;

        // 4턴 답변 부터 직접 계산
        if (turn > 3) {
            axes = calculateAxesForAdaptiveTurn(verdict);
        } else {
            // 고정 질문 조회
            FixedTurnRule fixedTurnRule = fixedTurnRuleRepository.findByCrimeTypeAndTurnNumber(type, turn);
            if (fixedTurnRule != null) {
                List<FixedTurnRuleOptions> optionList = fixedTurnRule.getOptions();
                for (FixedTurnRuleOptions option : optionList) {
                    if (Objects.equals(option.getText(), answer)) {
                        axes = option.getAxes();
                        break;
                    }
                }
            }
        }

        // 대화 저장
        DialogueLog dialogueLog = new DialogueLog();
        dialogueLog.setSession(simulationSession);
        dialogueLog.setSpeaker("user");
        dialogueLog.setMessageText(answer);
        dialogueLog.setTurnNumber(turn);
        dialogueLog.setVerdict(verdict);
        dialogueLog.setAxisAuthority(BigDecimal.valueOf(Objects.requireNonNull(axes).get(0)));
        dialogueLog.setAxisUrgency(BigDecimal.valueOf(Objects.requireNonNull(axes).get(1)));
        dialogueLog.setAxisLinkTrust(BigDecimal.valueOf(Objects.requireNonNull(axes).get(2)));
        dialogueLog.setAxisNoCallback(BigDecimal.valueOf(Objects.requireNonNull(axes).get(3)));

        //TODO 로직 확인
        double salience = calculateSalience(turn, turn, verdict, axes);
        dialogueLog.setSalience(BigDecimal.valueOf(salience));
        dialogueLog.setCreatedAt(Instant.now());
        dialogueLogRepository.save(dialogueLog);

        // 신규 질문 생성 1 ~ 2턴 일 경우에만
        int nextTurn = turn + 1;
        if (turn < 3) {
            FixedTurnRule newFixedTurnRule = fixedTurnRuleRepository.findByCrimeTypeAndTurnNumber(type, nextTurn);
            if (newFixedTurnRule != null) {
                List<SimulationSubRes> answers = new ArrayList<>();
                simulationRes.setTurn(nextTurn);
                simulationRes.setSpeech(newFixedTurnRule.getAiSpeech());
                List<FixedTurnRuleOptions> optionList = newFixedTurnRule.getOptions();
                for (FixedTurnRuleOptions option : optionList) {
                    SimulationSubRes optionRes = new SimulationSubRes();
                    optionRes.setAnswer(option.getText());
                    optionRes.setVerdict(option.getVerdict());
                    answers.add(optionRes);
                }
                simulationRes.setAnswers(answers);
                simulationRes.setSessionId(sessionId);

                // 신규 질문 대화 저장
                DialogueLog newDialogueLog = new DialogueLog();
                newDialogueLog.setSession(simulationSession);
                newDialogueLog.setSpeaker("agent");
                newDialogueLog.setMessageText(newFixedTurnRule.getAiSpeech());
                newDialogueLog.setTurnNumber(nextTurn);
                newDialogueLog.setCreatedAt(Instant.now());
                dialogueLogRepository.save(newDialogueLog);
            }
        } else if (turn < 9) {
            // 요청 데이터 세팅
            CrimeRequest request = new CrimeRequest();
            request.setCrime_type(type);

            // 유저 이름
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
                com.news.newsdata.simulation.req.UserInfo userinfo = new com.news.newsdata.simulation.req.UserInfo();
                userinfo.setUser_name(userDetails.getUser().getName());
                request.setUser_info(userinfo);
            }

            // 전체 질문 조회
            List<Map<String, Double>> axesList = new ArrayList<>();
            List<DialogueLog> dialogueLogList = dialogueLogRepository.findBySession_IdOrderByCreatedAtAsc(sessionId);
            List<DialogueHistory> dialogueHistoryList = new ArrayList<>();
            for (DialogueLog log : dialogueLogList) {
                DialogueHistory dialogue = new DialogueHistory();
                dialogue.setRole(log.getSpeaker());
                dialogue.setText(log.getMessageText());
                dialogue.setVerdict(log.getVerdict());
                if (Objects.equals(log.getSpeaker(), "user")) {
                    Map<String, Double> axesMap = new HashMap<>();
                    axesMap.put("authority", log.getAxisAuthority().doubleValue());
                    axesMap.put("urgency", log.getAxisUrgency().doubleValue());
                    axesMap.put("link_trust", log.getAxisLinkTrust().doubleValue());
                    axesMap.put("no_callback", log.getAxisNoCallback().doubleValue());
                    dialogue.setAxes(axesMap);
                    axesList.add(axesMap);
                }
                dialogueHistoryList.add(dialogue);
            }

            Map<String, Double> sumMap = new HashMap<>();
            for (Map<String, Double> axesTemp : axesList) {
                for (Map.Entry<String, Double> entry : axesTemp.entrySet()) {
                    sumMap.merge(entry.getKey(), entry.getValue(), Double::sum);
                }
            }
            // 총합이 가장 큰 key 찾기
            String maxKey = Collections.max(sumMap.entrySet(), Map.Entry.comparingByValue()).getKey();
            request.setHighest_vulnerability_axis(maxKey);
            request.setDialogue_history(dialogueHistoryList);

            // api 호출
            CrimeResponse crimeResponse = callApi("https://safeguard-ai-service-909778823628.us-central1.run.app/simulation/adaptive_turn", request);

            // 신규 질문 대화 저장
            DialogueLog newDialogueLog = new DialogueLog();
            newDialogueLog.setSession(simulationSession);
            newDialogueLog.setSpeaker("agent");
            newDialogueLog.setMessageText(Objects.requireNonNull(crimeResponse).getNext_speech());
            newDialogueLog.setTurnNumber(nextTurn);
            newDialogueLog.setCreatedAt(Instant.now());
            dialogueLogRepository.save(newDialogueLog);

            // 응답 처리
            List<SimulationSubRes> answers = new ArrayList<>();
            List<Option> optionList = Objects.requireNonNull(crimeResponse).getOptions();
            for (Option option : optionList) {
                SimulationSubRes optionRes = new SimulationSubRes();
                optionRes.setAnswer(option.getText());
                optionRes.setVerdict(option.getVerdict());
                answers.add(optionRes);
            }
            simulationRes.setSessionId(sessionId);
            simulationRes.setAnswers(answers);
            simulationRes.setTurn(nextTurn);
            simulationRes.setSpeech(crimeResponse.getNext_speech());
        }

        return simulationRes;
    }

    private CrimeResponse callApi(String url, CrimeRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CrimeRequest> entity = new HttpEntity<>(request, headers);

            // ----- POST 요청 -----
            ResponseEntity<CrimeResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    CrimeResponse.class
            );
            // ----- 응답 처리 -----
            return response.getBody();
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage());
        }
        return null;
    }

    private double calculateSalience(int turnIndex, int totalTurns, String verdict, List<Double> axesScores) {
        // Recency (최신성): 최신 턴과의 거리를 기반으로 계산
        int distanceFromLatest = (totalTurns - 1) - turnIndex;
        double recency = 1.0 / (1 + distanceFromLatest);

        // Error (위험 행동 여부)
        double error = ("risky".equals(verdict) || "unsafe".equals(verdict)) ? 1.0 : 0.0;

        // Axis Max (최대 취약점)
        double axisMax = 0.0;
        if (axesScores != null && !axesScores.isEmpty()) {
            axisMax = axesScores.stream()
                    .max(Double::compareTo)
                    .orElse(0.0);
        }
        // 최종 선형 결합
        double salience = (0.4 * recency) + (0.4 * error) + (0.2 * axisMax);

        // 최종값은 0과 1 사이로 고정 (Clipping)
        return Math.min(Math.max(salience, 0.0), 1.0);
    }

    /**
     * 적응형 턴(4~8턴)에서, 사용자가 선택한 선택지의 verdict를 기반으로
     * 기본 axes 점수를 계산합니다.
     *
     * @param verdict AI가 생성한 선택지에 포함된 판정값 ("safe", "risky", "unsafe")
     * @return 4개 축의 점수가 담긴 Map 객체
     */
    private List<Double> calculateAxesForAdaptiveTurn(String verdict) {
        List<Double> axesScores = new ArrayList<>();

        // verdict 값에 따라 미리 정의된 점수를 할당
        if ("safe".equals(verdict)) {
            axesScores.add(0.1); // authority
            axesScores.add(0.1); // urgency
            axesScores.add(0.1); // link_trust
            axesScores.add(0.1); // no_callback
        } else if ("risky".equals(verdict)) {
            axesScores.add(0.4); // authority
            axesScores.add(0.4); // urgency
            axesScores.add(0.4); // link_trust
            axesScores.add(0.4); // no_callback
        } else if ("unsafe".equals(verdict)) {
            axesScores.add(0.8); // authority
            axesScores.add(0.8); // urgency
            axesScores.add(0.8); // link_trust
            axesScores.add(0.8); // no_callback
        } else {
            // 예외 처리: 혹시 모를 비정상적인 verdict 값에 대한 기본값
            axesScores.add(0.0); // authority
            axesScores.add(0.0); // urgency
            axesScores.add(0.0); // link_trust
            axesScores.add(0.0); // no_callback
        }
        return axesScores;
    }

    public SimulationVoiceRes voiceFirstTurn(String type) throws IOException {
        SimulationVoiceRes simulationVoiceRes = new SimulationVoiceRes();

        // 시뮬레이션 Session 생성
        String uuid = UUID.randomUUID().toString();
        SimulationSession simulationSession = new SimulationSession();
        simulationSession.setId(uuid);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            simulationSession.setUser(userDetails.getUser());
        }

        // 타입에 맞는 첫번째 고정 질문 가져옴

        FixedTurnRule fixedTurnRule = fixedTurnRuleRepository.findByCrimeTypeAndTurnNumber(type, 1);
        if (fixedTurnRule != null) {
            String text = fixedTurnRule.getAiSpeech().replace("김민준", simulationSession.getUser().getName());
            simulationVoiceRes.setTurn(fixedTurnRule.getTurnNumber());
            simulationVoiceRes.setSpeech(text);
            simulationVoiceRes.setVoiceSpeech(Base64.encodeBase64String(textToSpeechService.synthesizeSpeech(text)));
            simulationVoiceRes.setSessionId(uuid);
        }

        simulationSession.setStatus("IN_PROGRESS");
        simulationSession.setCrimeType(type);
        simulationSession.setCreatedAt(Instant.now());
        simulationSessionRepository.save(simulationSession);

        // 대화 저장
        DialogueLog dialogueLog = new DialogueLog();
        dialogueLog.setSession(simulationSession);
        dialogueLog.setSpeaker("agent");
        dialogueLog.setMessageText(Objects.requireNonNull(fixedTurnRule).getAiSpeech());
        dialogueLog.setTurnNumber(Objects.requireNonNull(fixedTurnRule).getTurnNumber());
        dialogueLog.setCreatedAt(Instant.now());
        dialogueLogRepository.save(dialogueLog);

        return simulationVoiceRes;
    }

    public SimulationVoiceRes voiceTurn(SimulationVoiceReq simulationVoiceReq) throws IOException {
        SimulationVoiceRes simulationVoiceRes = new SimulationVoiceRes();
        String sessionId = simulationVoiceReq.getSessionId();
        int turn = simulationVoiceReq.getTurn();
        String answer = simulationVoiceReq.getAnswer();

        SimulationSession simulationSession = simulationSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        int nextTurn = turn + 1;

        if (turn < 9) {
            // 요청 데이터 세팅
            String url = "https://safeguard-ai-service-909778823628.us-central1.run.app/simulation/voice_turn"; // 실제 API 주소로 변경

            // JSON Body 구성
            ApiRequestDto apiRequestDto = new ApiRequestDto();
            apiRequestDto.setUser_message(answer);

            com.news.newsdata.simulation.req.UserInfo userInfo = new com.news.newsdata.simulation.req.UserInfo();
            // 유저 이름
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
                userInfo.setUser_name(userDetails.getUser().getName());
            }
            apiRequestDto.setUser_info(userInfo);

            // 전체 질문 조회
            List<DialogueLog> dialogueLogList = dialogueLogRepository.findBySession_IdOrderByCreatedAtAsc(sessionId);
            List<DialogueTurn> dialogueHistory = new ArrayList<>();
            for (DialogueLog log : dialogueLogList) {
                DialogueTurn dialogueTurn = new DialogueTurn();
                dialogueTurn.setRole(log.getSpeaker());
                dialogueTurn.setText(log.getMessageText());
                dialogueHistory.add(dialogueTurn);
            }
            apiRequestDto.setDialogue_history(dialogueHistory);

            // Header 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HttpEntity
            HttpEntity<ApiRequestDto> requestEntity = new HttpEntity<>(apiRequestDto, headers);

            // POST 요청
            ResponseEntity<ResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResponseDto.class
            );
            String speech = Objects.requireNonNull(response.getBody()).getAi_message();

            // 대화 저장
            DialogueLog dialogueLog = new DialogueLog();
            dialogueLog.setSession(simulationSession);
            dialogueLog.setSpeaker("user");
            dialogueLog.setMessageText(answer);
            dialogueLog.setTurnNumber(turn);
            dialogueLog.setCreatedAt(Instant.now());
            dialogueLogRepository.save(dialogueLog);

            // 신규 질문 대화 저장
            DialogueLog newDialogueLog = new DialogueLog();
            newDialogueLog.setSession(simulationSession);
            newDialogueLog.setSpeaker("agent");
            newDialogueLog.setMessageText(speech);
            newDialogueLog.setTurnNumber(nextTurn);
            newDialogueLog.setCreatedAt(Instant.now());
            dialogueLogRepository.save(newDialogueLog);

            // 응답 처리
            simulationVoiceRes.setVoiceSpeech(Base64.encodeBase64String(textToSpeechService.synthesizeSpeech(speech)));
            simulationVoiceRes.setTurn(nextTurn);
            simulationVoiceRes.setSpeech(speech);
            simulationVoiceRes.setSessionId(sessionId);
        } else {
            // 대화 저장
            DialogueLog dialogueLog = new DialogueLog();
            dialogueLog.setSession(simulationSession);
            dialogueLog.setSpeaker("user");
            dialogueLog.setMessageText(answer);
            dialogueLog.setTurnNumber(turn);
            dialogueLog.setCreatedAt(Instant.now());
            dialogueLogRepository.save(dialogueLog);
        }

        return simulationVoiceRes;
    }

    public SimulationReportRes report(SimulationReportReq simulationReportReq) {
        String url = "https://safeguard-ai-service-909778823628.us-central1.run.app/analysis/basic_report"; // 실제 API 주소로 변경
        String sessionId = simulationReportReq.getSessionId();

        // JSON Body 구성
        Map<String, Object> body = new HashMap<>();
        SimulationSession simulationSession = simulationSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        body.put("crime_type", simulationSession.getCrimeType());

        Map<String, Object> userInfo = new HashMap<>();
        UserInfo user = null;
        // 유저 이름
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            user = userDetails.getUser();
        }
        userInfo.put("user_name", Objects.requireNonNull(user).getName());
        body.put("user_info", userInfo);

        // 전체 질문 조회
        List<DialogueLog> dialogueLogList = dialogueLogRepository.findBySession_IdOrderByCreatedAtAsc(sessionId);
        List<Map<String, Object>> dialogueHistory = new ArrayList<>();
        for (DialogueLog log : dialogueLogList) {
            Map<String, Object> historyEntry = new HashMap<>();
            historyEntry.put("role", log.getSpeaker());
            historyEntry.put("text", log.getMessageText());
            dialogueHistory.add(historyEntry);
        }
        body.put("dialogue_history", dialogueHistory);

        // Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // POST 요청
        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        Map<String, Object> resBody = response.getBody();
        // 값 꺼내기
        String grade = (String) Objects.requireNonNull(resBody).get("grade");
        String summary = (String) resBody.get("summary");
        String cautionPoint = (String) resBody.get("caution_point");
        String guide = (String) resBody.get("guide");

        AnalysisGeneralResult analysisGeneralResult = new AnalysisGeneralResult();
        analysisGeneralResult.setGrade(grade);
        analysisGeneralResult.setSummary(summary);
        analysisGeneralResult.setCautionPoint(cautionPoint);
        analysisGeneralResult.setGuide(guide);
        analysisGeneralResult.setUser(user);
        analysisGeneralResult.setSession(simulationSession);
        analysisGeneralResult.setCreatedAt(Instant.now());
        analysisGeneralResultRepository.save(analysisGeneralResult);

        SimulationReportRes simulationReportRes = new SimulationReportRes();
        simulationReportRes.setId(analysisGeneralResult.getId());
        simulationReportRes.setGrade(grade);
        simulationReportRes.setSummary(summary);
        simulationReportRes.setCautionPoint(cautionPoint);
        simulationReportRes.setGuide(guide);
        simulationReportRes.setCreatedAt(analysisGeneralResult.getCreatedAt());
        return simulationReportRes;

    }

    public SimulationDeepReportRes deepReport(SimulationDeepReportReq simulationDeepReportReq) {
        Long repostId = Long.parseLong(simulationDeepReportReq.getReportId());
        AnalysisGeneralResult analysisGeneralResult = analysisGeneralResultRepository.findById(repostId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        UserInfo user = analysisGeneralResult.getUser();
        SimulationSession simulationSession = analysisGeneralResult.getSession();
        String sessionId = simulationSession.getId();
        String url = "https://safeguard-ai-service-909778823628.us-central1.run.app/analysis/premium_report"; // 실제 API 주소로 변경

        // JSON Body 구성
        Map<String, Object> body = new HashMap<>();

        body.put("crime_type", simulationSession.getCrimeType());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user_name", Objects.requireNonNull(user).getName());
        body.put("user_info", userInfo);

        // 전체 질문 조회
        List<DialogueLog> dialogueLogList = dialogueLogRepository.findBySession_IdOrderByCreatedAtAsc(sessionId);
        List<Map<String, Object>> dialogueHistory = new ArrayList<>();
        for (DialogueLog log : dialogueLogList) {
            Map<String, Object> historyEntry = new HashMap<>();
            historyEntry.put("role", log.getSpeaker());
            historyEntry.put("text", log.getMessageText());
            dialogueHistory.add(historyEntry);
        }
        body.put("dialogue_history", dialogueHistory);

        // Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // POST 요청
        ResponseEntity<ApiDeepReportRes> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                ApiDeepReportRes.class
        );

        ApiDeepReportRes resBody = response.getBody();
        AnalysisDeepResult analysisDeepResult = new AnalysisDeepResult();
        analysisDeepResult.setGrade(Objects.requireNonNull(resBody).getOverall_evaluation().getGrade());
        analysisDeepResult.setSummary(resBody.getOverall_evaluation().getSummary());
        analysisDeepResult.setRecommendedAction(resBody.getRecommended_action());
        analysisDeepResult.setUser(user);
        analysisDeepResult.setSession(simulationSession);
        analysisDeepResult.setReport(analysisGeneralResult);
        analysisDeepResultRepository.save(analysisDeepResult);
        List<SimulationDeepDetailReportRes> detailList = new ArrayList<>();
        resBody.getCritical_moments().forEach(criticalMomentData -> {
            AnalysisCriticalMoment analysisCriticalMoment = new AnalysisCriticalMoment();
            analysisCriticalMoment.setDeepResult(analysisDeepResult);
            analysisCriticalMoment.setTurnNumber(criticalMomentData.getTurn_number());
            analysisCriticalMoment.setRiskAnalysis(criticalMomentData.getRisk_analysis());
            analysisCriticalMoment.setUserMessage(criticalMomentData.getUser_message());
            analysisCriticalMoment.setLegalAdvice(criticalMomentData.getLegal_advice());

            SimulationDeepDetailReportRes simulationDeepDetailReportRes = new SimulationDeepDetailReportRes();
            simulationDeepDetailReportRes.setTurnNumber(criticalMomentData.getTurn_number());
            simulationDeepDetailReportRes.setRiskAnalysis(criticalMomentData.getRisk_analysis());
            simulationDeepDetailReportRes.setUserMessage(criticalMomentData.getUser_message());
            simulationDeepDetailReportRes.setLegalAdvice(criticalMomentData.getLegal_advice());
            simulationDeepDetailReportRes.setId(analysisCriticalMoment.getId());
            detailList.add(simulationDeepDetailReportRes);
            analysisCriticalMomentRepository.save(analysisCriticalMoment);
        });

        SimulationDeepReportRes simulationDeepReportRes = new SimulationDeepReportRes();
        simulationDeepReportRes.setGrade(Objects.requireNonNull(response.getBody()).getOverall_evaluation().getGrade());
        simulationDeepReportRes.setRecommendedAction(response.getBody().getRecommended_action());
        simulationDeepReportRes.setSummary(response.getBody().getOverall_evaluation().getSummary());
        simulationDeepReportRes.setDetailList(detailList);
        simulationDeepReportRes.setCreatedAt(analysisDeepResult.getCreatedAt());
        return simulationDeepReportRes;
    }

    public SimulationImageRes image(MultipartFile file) {
        RestTemplate restTemplate = new RestTemplate();

        // Multipart body 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("image_file", resource);

        } catch (IOException e) {
            throw new RuntimeException("파일 변환 실패", e);
        }

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // HttpEntity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 전송할 URL
        String url = "https://safeguard-ai-service-909778823628.us-central1.run.app/diagnose/image"; // 실제 API 주소

        ResponseEntity<AnalysisRiskResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                AnalysisRiskResponse .class
        );
        AnalysisRiskResponse analysisRiskResponse =  response.getBody();

        UserInfo user = null;
        // 유저 이름
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            user = userDetails.getUser();
        }
        AnalysisRisk analysisRisk = new AnalysisRisk();
        analysisRisk.setUser(user);
        analysisRisk.setGuide(Objects.requireNonNull(analysisRiskResponse).getGuide());
        analysisRisk.setSummary(analysisRiskResponse.getSummary());
        analysisRisk.setRiskLevel(analysisRiskResponse.getRisk_level());
        analysisRisk.setTitle(analysisRiskResponse.getTitle());
        analysisRisk.setDetectedKeywords(String.join(",", analysisRiskResponse.getDetected_keywords()));
        analysisRisk.setExtractedText(analysisRiskResponse.getExtracted_text());
        analysisRiskRepository.save(analysisRisk);

        SimulationImageRes simulationImageRes = new SimulationImageRes();
        simulationImageRes.setTitle(analysisRiskResponse.getTitle());
        simulationImageRes.setSummary(analysisRiskResponse.getSummary());
        simulationImageRes.setGuide(analysisRiskResponse.getGuide());
        simulationImageRes.setRiskLevel(analysisRiskResponse.getRisk_level());
        simulationImageRes.setDetectedKeywords(analysisRiskResponse.getDetected_keywords());
        simulationImageRes.setExtractedText(analysisRiskResponse.getExtracted_text());

        return simulationImageRes;
    }

    public ResultListRes resultList() {
        ResultListRes resultListRes = new ResultListRes();
        Long userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            userId = userDetails.getUser().getId();
        }

        List<ResultReport> reportList = new ArrayList<>();
        analysisGeneralResultRepository.findByUser_Id(userId).forEach( analysisGeneralResult -> {
            ResultReport resultReport = new ResultReport();
            resultReport.setId(analysisGeneralResult.getId());
            resultReport.setTitle(analysisGeneralResult.getSession().getCrimeType() + " : " + "나의 금융 면연력 리포트");
            resultReport.setCreatedAt(analysisGeneralResult.getCreatedAt());
            reportList.add(resultReport);
        });
        resultListRes.setReportList(reportList);

        List<ResultReport> deepReportList = new ArrayList<>();
        analysisDeepResultRepository.findByUser_Id(userId).forEach(analysisDeepResult -> {
            ResultReport resultReport = new ResultReport();
            resultReport.setId(analysisDeepResult.getId());
            resultReport.setTitle(analysisDeepResult.getSession().getCrimeType() + " : " + "AI 변호사 심층 분석 리포트");
            resultReport.setCreatedAt(analysisDeepResult.getCreatedAt());
            deepReportList.add(resultReport);
        });
        resultListRes.setDeepReportList(deepReportList);

        List<ResultReport> imageReportList = new ArrayList<>();
        analysisRiskRepository.findByUser_Id(userId).forEach(analysisRiskItem -> {
            ResultReport resultReport = new ResultReport();
            resultReport.setId(analysisRiskItem.getId());
            resultReport.setTitle(analysisRiskItem.getTitle() + " : " + "실시간 위험 진단 결과");
            resultReport.setCreatedAt(analysisRiskItem.getCreatedAt());
            imageReportList.add(resultReport);
        });
        resultListRes.setImageReportList(imageReportList);
        return resultListRes;
    }

    public SimulationReportRes reportInfo(Long reportId) {
        AnalysisGeneralResult analysisGeneralResult = analysisGeneralResultRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        SimulationReportRes simulationReportRes = new SimulationReportRes();
        simulationReportRes.setGrade(analysisGeneralResult.getGrade());
        simulationReportRes.setSummary(analysisGeneralResult.getSummary());
        simulationReportRes.setCautionPoint(analysisGeneralResult.getCautionPoint());
        simulationReportRes.setGuide(analysisGeneralResult.getGuide());
        return simulationReportRes;
    }

    public SimulationDeepReportRes deepReportInfo(Long reportId) {
        AnalysisDeepResult analysisDeepResult = analysisDeepResultRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        SimulationDeepReportRes simulationDeepReportRes = new SimulationDeepReportRes();
        simulationDeepReportRes.setGrade(analysisDeepResult.getGrade());
        simulationDeepReportRes.setSummary(analysisDeepResult.getSummary());
        simulationDeepReportRes.setRecommendedAction(analysisDeepResult.getRecommendedAction());

        List<SimulationDeepDetailReportRes> detailList = new ArrayList<>();
        analysisDeepResult.getAnalysisCriticalMoments().forEach(criticalMoment -> {
            SimulationDeepDetailReportRes detail = new SimulationDeepDetailReportRes();
            detail.setTurnNumber(criticalMoment.getTurnNumber());
            detail.setRiskAnalysis(criticalMoment.getRiskAnalysis());
            detail.setUserMessage(criticalMoment.getUserMessage());
            detail.setLegalAdvice(criticalMoment.getLegalAdvice());
            detailList.add(detail);
        });
        simulationDeepReportRes.setDetailList(detailList);
        return simulationDeepReportRes;
    }

    public SimulationImageRes imageReportInfo(Long reportId) {
        AnalysisRisk analysisRisk = analysisRiskRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        SimulationImageRes simulationImageRes = new SimulationImageRes();
        simulationImageRes.setTitle(analysisRisk.getTitle());
        simulationImageRes.setSummary(analysisRisk.getSummary());
        simulationImageRes.setGuide(analysisRisk.getGuide());
        simulationImageRes.setRiskLevel(analysisRisk.getRiskLevel());
        simulationImageRes.setDetectedKeywords(Arrays.asList(analysisRisk.getDetectedKeywords().split(",")));
        simulationImageRes.setExtractedText(analysisRisk.getExtractedText());

        return simulationImageRes;
    }

    public List<SimulationCategoryRes> category(String voiceYn) {
        List<SimulationCategoryRes> categoryList = new ArrayList<>();
        simulationCategoryRepository.findByVoiceYnAndUseYnOrderByIdAsc(voiceYn, "Y").forEach(simulationCategory -> {
            SimulationCategoryRes simulationCategoryRes = new SimulationCategoryRes();
            simulationCategoryRes.setCategoryId(simulationCategory.getId());
            simulationCategoryRes.setCatgoryName(simulationCategory.getCatgoryName());
            simulationCategoryRes.setCategoryContent(simulationCategory.getCategoryContent());
            categoryList.add(simulationCategoryRes);
        });
        return categoryList;
    }

    public SimulationRes text(int type) {
        SimulationRes simulationRes = new SimulationRes();
        FixedTurnRule fixedTurnRule = fixedTurnRuleRepository.findByCrimeTypeAndTurnNumber(String.valueOf(type), 1);
        if (fixedTurnRule != null) {
            simulationRes.setTurn(fixedTurnRule.getTurnNumber());
            simulationRes.setSpeech(fixedTurnRule.getAiSpeech());
            List<SimulationSubRes> answers = new ArrayList<>();
            List<FixedTurnRuleOptions> optionList = fixedTurnRule.getOptions();
            for (FixedTurnRuleOptions option : optionList) {
                SimulationSubRes optionRes = new SimulationSubRes();
                optionRes.setAnswer(option.getText());
                optionRes.setVerdict(option.getVerdict());
                answers.add(optionRes);
            }
            simulationRes.setAnswers(answers);
        }
        return simulationRes;
    }
}
