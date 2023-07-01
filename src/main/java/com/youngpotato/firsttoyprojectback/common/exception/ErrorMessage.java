package com.youngpotato.firsttoyprojectback.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorMessage {

    // COMMON
    TEMPORARY_SERVER_ERROR(400, "C007", "Temporary Server Error"),
    DATA_NOT_FOUND(404, "C011", "자료가 없습니다."),
    SAVE_ERROR(400, "C012", "저장 시 오류가 발생하였습니다."),
    NOT_AUTHORIZED(403, "C013", "권한이 없습니다."),

    // Valid
    EMAIL_NOT_VALID(400, "V001", "이메일 형식이 아닙니다."),
    USER_ID_NOT_VALID(400, "V002", "사용자 아이디는 이메일을 사용하여 주세요."),

    // USER
    USER_EXISTS(400, "U001", "이미 존재하는 사용자 입니다."),
    USER_NOT_FOUND(400, "U002", "사용자를 찾을 수 없습니다.");

    // DOC

    private int status;
    private String code;
    private String message;

    ErrorMessage(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
