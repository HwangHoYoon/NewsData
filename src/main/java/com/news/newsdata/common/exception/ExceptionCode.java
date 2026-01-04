package com.news.newsdata.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@AllArgsConstructor
public enum ExceptionCode {

    BAD_REQUEST(String.valueOf(HttpStatus.BAD_REQUEST.value()), "잘못된 요청입니다."),

    NOT_FOUND(String.valueOf(HttpStatus.NOT_FOUND.value()), "요청한 페이지를 찾을 수 없습니다."),

    SERVER_ERROR(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "내부 서버 오류입니다."),

    // Custom Exception
    TOKEN_NULL("401", "권한이 없습니다."),

    SECURITY("800", "로그인이 필요합니다"),

    USER_NULL("801", "유저 정보가 올바르지 않습니다."),

    FILE_SIZE("852", "파일 업로드 최대 크기는 50M 입니다."),

    API_NULL("980", "API 결과 NULL"),

    API_JSON_MAPPING_FAIL("981", "API JSON 매핑에 실패했습니다."),

    API_NOT_OK("982", "API 성공 응답이 아닙니다."),

    API_TOKEN_COUNT_OVER("987", "글자수를 5000자 이하로 줄여주세요.")

    ;

    private static final ExceptionCode[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String message;

    public static ExceptionCode resolve(String statusCode) {
        for (ExceptionCode status : VALUES) {
            if (status.code.equals(statusCode)) {
                return status;
            }
        }
        return null;
    }
}
