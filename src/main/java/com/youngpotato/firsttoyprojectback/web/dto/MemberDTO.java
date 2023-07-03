package com.youngpotato.firsttoyprojectback.web.dto;

import com.youngpotato.firsttoyprojectback.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MemberDTO {

    private String email;

    private LocalDateTime cre_time;

    private LocalDateTime upd_time;

    private String nickname;

    private String phoneNum;

    private LocalDate birthDate;

    private String address;

    private List<String> roles;

    public static MemberDTO from(Member member) {
        return MemberDTO.builder()
                .email(member.getEmail())
                .cre_time(member.getCreTime())
                .upd_time(member.getUpdTime())
                .nickname(member.getNickname())
                .phoneNum(member.getPhoneNum())
                .birthDate(member.getBirthDate())
                .address(member.getAddress())
                .roles(member.getRoleList())
                .build();
    }
}
