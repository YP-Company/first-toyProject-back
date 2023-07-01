package com.youngpotato.firsttoyprojectback.web.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SignUpDTO {

    private String email;

    private String password;

    private String nickname;

    private String phoneNum;

    private LocalDate birthDate;

    private String address;
}
