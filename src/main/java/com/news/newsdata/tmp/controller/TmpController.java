package com.news.newsdata.tmp.controller;

import com.news.newsdata.api.google.ocr.service.OcrService;
import com.news.newsdata.api.google.tts.TextToSpeechService;
import com.news.newsdata.common.exception.CommonException;
import com.news.newsdata.tmp.entity.Tmp;
import com.news.newsdata.tmp.service.TmpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Tmp", description = "Tmp API")
public class TmpController {
    private final TmpService tmpService;

    private final OcrService ocrService;

    private final TextToSpeechService textToSpeechService;

    @Operation(summary = "Tmp 목록 조회 (기본 테스트)", description = "Tmp 목록 조회 (기본 테스트)")
    @GetMapping("/")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")}
    )
    public List<Tmp> getTmpList() throws Exception {
        // 테스트1
        return tmpService.selectTmpList();
    }

    @Operation(summary = "ocr 등록", description = "ocr 등록")
    @PostMapping(path = "/ocr", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    }
    )
    public ResponseEntity<?> insertInterview(
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {
        String saveName = "C:/tmp/img/" + UUID.randomUUID();
        Path savePath = Paths.get(saveName);
        file.transferTo(savePath);
        String text = ocrService.ocrImageFile(savePath.toFile());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(text);
    }

    @GetMapping("/tts")
    @ApiResponse(
            responseCode = "200",
            description = "음성 파일 응답",
            content = @Content(
                    mediaType = "audio/wav",
                    schema = @Schema(type = "string", format = "binary")
            )
    )
    public ResponseEntity<?> chatToAudio(
            String text
    ) {
        if (text == null || text.trim().isEmpty()) {
            throw new CommonException("Text parameter is required.", HttpStatus.BAD_REQUEST.name());
        }
        try {
            byte[] audioBytes = textToSpeechService.synthesizeSpeech(text);
            String base64Audio = Base64.encodeBase64String(audioBytes);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"audio.wav\"")
                    .contentType(MediaType.parseMediaType("audio/wav"))
                    .body(audioBytes);
        } catch (IllegalArgumentException e) {
            // 예를 들어 audioEncoding 파라미터가 유효하지 않은 경우
            throw new CommonException("Invalid parameter: " + e.getMessage(), HttpStatus.BAD_REQUEST.name());
        } catch (IOException e) {
            throw new CommonException("Failed to synthesize speech: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.name());
        }
    }
}
