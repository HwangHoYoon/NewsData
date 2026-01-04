package com.news.newsdata.api.google.tts;

import com.google.cloud.texttospeech.v1.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TextToSpeechService {

    private final String languageCode = "ko-KR"; // 기본 언어 코드 (한국어)
    private final String voiceName = "ko-KR-Wavenet-D"; // 기본 음성 이름 (한국어 Wavenet 음성)
    private final String ssmlGender = "MALE"; // 기본 성별 (중립)
    private final String audioEncoding = "MP3"; // 기본 오디오 인코딩 (MP3)
    private final double speakingRate = 1.0; // 기본 음성 속도 (1.0)
    private final double pitch = 0.0; // 기본 음성 피치 (0.0)

    /**
     * 텍스트를 음성으로 변환하여 오디오 바이트 배열로 반환합니다.
     *
     * @param text 음성으로 변환할 텍스트
     * @return 오디오 데이터 바이트 배열
     * @throws IOException
     */
    public byte[] synthesizeSpeech(String text) throws IOException {

        // 텍스트 입력 설정
        SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

        // 음성 선택 설정
        VoiceSelectionParams.Builder voiceBuilder = VoiceSelectionParams.newBuilder()
                .setLanguageCode(languageCode);
        voiceBuilder.setName(voiceName);
        try {
            SsmlVoiceGender gender = SsmlVoiceGender.valueOf(ssmlGender.toUpperCase());
            voiceBuilder.setSsmlGender(gender);
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 SSML Gender 값이 들어온 경우 처리
            System.err.println("Invalid SSML Gender: " + ssmlGender + ". Using default.");
        }

        // 오디오 설정
        AudioConfig.Builder audioConfigBuilder = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.valueOf(audioEncoding.toUpperCase())); // MP3, LINEAR16 (WAV), OGG_OPUS 등
        audioConfigBuilder.setSpeakingRate((float) speakingRate);
        audioConfigBuilder.setPitch((float) pitch);

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // 음성 합성 API 호출
            com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voiceBuilder.build(), audioConfigBuilder.build());

            // 오디오 콘텐츠 반환
            return response.getAudioContent().toByteArray();
        }
    }
}
