package com.news.newsdata.simulation.controller;

import com.news.newsdata.simulation.req.SimulationDeepReportReq;
import com.news.newsdata.simulation.req.SimulationReportReq;
import com.news.newsdata.simulation.req.SimulationReq;
import com.news.newsdata.simulation.req.SimulationVoiceReq;
import com.news.newsdata.simulation.res.*;
import com.news.newsdata.simulation.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;

    @Operation(summary = "카테고리 조회", description = "카테고리 조회")
    @GetMapping("/category")
    public ResponseEntity<List<SimulationCategoryRes>> category (
    ) {
        List<SimulationCategoryRes> simulationCategoryRes = simulationService.category("N");
        return ResponseEntity.ok(simulationCategoryRes);
    }

    @Operation(summary = "텍스트", description = "텍스트")
    @GetMapping("/text")
    public ResponseEntity<SimulationRes> text(
            @Schema(description = "type", example = "1", name = "type") @RequestParam(name = "type") int type
    ) {
        SimulationRes simulationRes = simulationService.text(type);
        return ResponseEntity.ok(simulationRes);
    }



    @Operation(summary = "텍스트 첫화면", description = "텍스트 첫화면")
    @GetMapping("/text/firstTurn")
    public ResponseEntity<SimulationRes> firstTurn(
            @Schema(description = "type", example = "전세사기", name = "type") @RequestParam(name = "type") String type
    ) {
        SimulationRes simulationRes = simulationService.firstTurn(type);
        return ResponseEntity.ok(simulationRes);
    }

    @Operation(summary = "텍스트 대화", description = "텍스트 대화")
    @PostMapping("/text/turn")
    public ResponseEntity<SimulationRes> turn(
            @RequestBody SimulationReq simulationReq) {
        SimulationRes simulationRes = simulationService.turn(simulationReq);
        return ResponseEntity.ok(simulationRes);
    }

    @Operation(summary = "음성 첫 화면", description = "음성 첫 화면")
    @GetMapping("/voice/firstTurn")
    public ResponseEntity<SimulationVoiceRes> voiceFirstTurn(
            @Schema(description = "type", example = "보이스피싱", name = "type") @RequestParam(name = "type") String type
    ) throws IOException {
        SimulationVoiceRes simulationVoiceRes = simulationService.voiceFirstTurn(type);
        return ResponseEntity.ok(simulationVoiceRes);
    }

    @Operation(summary = "음성 대화", description = "음성 대화")
    @PostMapping("/voice/turn")
    public ResponseEntity<SimulationVoiceRes> voiceTurn(
            @RequestBody SimulationVoiceReq simulationVoiceReq) throws IOException {
        SimulationVoiceRes simulationVoiceRes = simulationService.voiceTurn(simulationVoiceReq);
        return ResponseEntity.ok(simulationVoiceRes);
    }

    @Operation(summary = "일반 리포트 만들기", description = "일반 리포트 만들기")
    @PostMapping("/report")
    public ResponseEntity<SimulationReportRes> report(
            @RequestBody SimulationReportReq simulationReportReq) throws IOException {
        SimulationReportRes simulationReportRes = simulationService.report(simulationReportReq);
        return ResponseEntity.ok(simulationReportRes);
    }

    @Operation(summary = "심층 리포트 만들기", description = "심층 리포트 만들기")
    @PostMapping("/deepReport")
    public ResponseEntity<SimulationDeepReportRes> deepReport(
            @RequestBody SimulationDeepReportReq simulationDeepReportReq) throws IOException {
        SimulationDeepReportRes simulationDeepReportRes = simulationService.deepReport(simulationDeepReportReq);
        return ResponseEntity.ok(simulationDeepReportRes);
    }

    @Operation(summary = "실시간 OCR 위험진단", description = "실시간 OCR 위험진단")
    @PostMapping(path = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SimulationImageRes> image(
            @RequestPart(value = "file") MultipartFile file
    ) throws IOException {
        SimulationImageRes simulationImageRes = simulationService.image(file);
        return ResponseEntity.ok(simulationImageRes);
    }

    @Operation(summary = "결과 목록 조회", description = "결과 목록 조회")
    @GetMapping("/resultList")
    public ResponseEntity<ResultListRes> resultList(
    ) throws IOException {
        ResultListRes resultListRes = simulationService.resultList();
        return ResponseEntity.ok(resultListRes);
    }

    @Operation(summary = "일반 결과 조회", description = "일반 결과 조회")
    @GetMapping("/reportInfo")
    public ResponseEntity<SimulationReportRes> reportInfo(
            @Schema(description = "reportId", example = "1", name = "reportId") @RequestParam(name = "reportId") Long reportId
    ) throws IOException {
        SimulationReportRes simulationReportRes = simulationService.reportInfo(reportId);
        return ResponseEntity.ok(simulationReportRes);
    }

    @Operation(summary = "심층 결과 조회", description = "심층 결과 조회")
    @GetMapping("/deepReportInfo")
    public ResponseEntity<SimulationDeepReportRes> deepReportInfo(
            @Schema(description = "reportId", example = "1", name = "reportId") @RequestParam(name = "reportId") Long reportId
    ) throws IOException {
        SimulationDeepReportRes simulationDeepReportRes = simulationService.deepReportInfo(reportId);
        return ResponseEntity.ok(simulationDeepReportRes);
    }

    @Operation(summary = "실시간 위험 결과 조회", description = "실시간 위험 결과 조회")
    @GetMapping("/imageReportInfo")
    public ResponseEntity<SimulationImageRes> imageReportInfo(
            @Schema(description = "reportId", example = "1", name = "reportId") @RequestParam(name = "reportId") Long reportId
    ) throws IOException {
        SimulationImageRes simulationImageRes = simulationService.imageReportInfo(reportId);
        return ResponseEntity.ok(simulationImageRes);
    }


}
