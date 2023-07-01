package com.youngpotato.firsttoyprojectback.auth;

import com.youngpotato.firsttoyprojectback.domain.member.Member;
import com.youngpotato.firsttoyprojectback.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * http://localhost:8080/login
 * security의 기본적인 로그인 주소임
 * 동작을 안함
 */
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username);
        return new PrincipalDetails(member);
    }
}
