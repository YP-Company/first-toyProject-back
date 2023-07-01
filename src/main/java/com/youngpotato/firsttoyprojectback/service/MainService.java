package com.youngpotato.firsttoyprojectback.service;

import com.youngpotato.firsttoyprojectback.common.exception.ApiException;
import com.youngpotato.firsttoyprojectback.common.exception.ErrorMessage;
import com.youngpotato.firsttoyprojectback.domain.member.Member;
import com.youngpotato.firsttoyprojectback.domain.member.MemberRepository;
import com.youngpotato.firsttoyprojectback.web.dto.MemberDTO;
import com.youngpotato.firsttoyprojectback.web.dto.SignUpDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public MemberDTO signup(SignUpDTO signUpDto) {
        if (memberRepository.findByEmail(signUpDto.getEmail()) != null) {
            throw new ApiException(ErrorMessage.USER_EXISTS);
        }

        Member member = Member.builder()
                .email(signUpDto.getEmail())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .nickname(signUpDto.getNickname())
                .phoneNum(signUpDto.getPhoneNum())
                .birthDate(signUpDto.getBirthDate())
                .address(signUpDto.getAddress())
                .roles("ROLE_USER")
                .build();

        return MemberDTO.from(memberRepository.save(member));
    }
}
