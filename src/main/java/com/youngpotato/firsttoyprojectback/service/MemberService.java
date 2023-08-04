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

    /**
     * 일반 회원가입
     */
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

    /**
     * 소셜 로그인 (회원가입)
     */
    public Member oauthSignup(OAuth2UserInfo oAuth2UserInfo) {
        String provider = oAuth2UserInfo.getProvider(); // 플랫폼 명
        String providerId = oAuth2UserInfo.getProviderId(); // 플랫품 id
//        String username = provider + "_" + providerId;
        String nickName = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getEmail();
        String password = bCryptPasswordEncoder.encode("password"); // 무의미 값
        String role = "ROLE_USER";

        Member member = memberRepository.findByEmailAndProvider(email, provider);
        if (member == null) {
            member = Member.builder()
                    .email(email)
                    .password(password)
                    .nickname(nickName)
                    .roles(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            memberRepository.save(member);
        }

        return member;
    }
}
