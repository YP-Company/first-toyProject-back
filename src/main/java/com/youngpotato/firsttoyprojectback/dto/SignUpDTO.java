package com.youngpotato.firsttoyprojectback.dto;

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
