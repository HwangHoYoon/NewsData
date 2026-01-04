package com.news.newsdata.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum MessageCmmCode {

    OK("200", "OK"),

    SUCCESS_SAVE("600", "정상적으로 저장되었습니다."),

    SUCCESS_DELETE("601", "정상적으로 삭제되었습니다."),

    FAIL_SAVE("602", "저장에 실패하였습니다."),

    FAIL_DELETE("603", "삭제에 실패하였습니다."),

    SUCCESS("604", "정상적으로 처리되었습니다."),

    NEW_USER("700", "신규 가입되었습니다."),
    ;

    private static final MessageCmmCode[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String message;

    public static MessageCmmCode resolve(String statusCode) {
        for (MessageCmmCode status : VALUES) {
            if (status.code.equals(statusCode)) {
                return status;
            }
        }
        return null;
    }
}
