package com.youngpotato.firsttoyprojectback.service;

import com.youngpotato.firsttoyprojectback.common.Constants;
import com.youngpotato.firsttoyprojectback.common.auth.oauth2.provider.OAuth2UserInfo;
import com.youngpotato.firsttoyprojectback.common.exception.ApiException;
import com.youngpotato.firsttoyprojectback.common.exception.ErrorMessage;
import com.youngpotato.firsttoyprojectback.domain.member.Member;
import com.youngpotato.firsttoyprojectback.domain.member.MemberRepository;
import com.youngpotato.firsttoyprojectback.dto.MemberDTO;
import com.youngpotato.firsttoyprojectback.dto.SignUpDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberDTO signup(SignUpDTO signUpDto) {
        // 중복 회원가입 처리
        if (memberRepository.findByEmailAndProvider(signUpDto.getEmail(), Constants.SYSTEM_STRING) != null) {
            throw new ApiException(ErrorMessage.USER_EXISTS);
        }

        // 회원가입
        Member member = Member.builder()
                .email(signUpDto.getEmail())
                .password(signUpDto.getPassword())
                .password(bCryptPasswordEncoder.encode(signUpDto.getPassword()))
                .nickname(signUpDto.getNickname())
                .phoneNum(signUpDto.getPhoneNum())
                .birthDate(signUpDto.getBirthDate())
                .address(signUpDto.getAddress())
                .roles("ROLE_USER")
                .provider(Constants.SYSTEM_STRING)
                .build();

        return MemberDTO.from(memberRepository.save(member));
    }
}
