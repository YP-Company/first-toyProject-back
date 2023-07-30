package com.youngpotato.firsttoyprojectback.common.auth;

import com.youngpotato.firsttoyprojectback.common.Constants;
import com.youngpotato.firsttoyprojectback.domain.member.Member;
import com.youngpotato.firsttoyprojectback.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Security 설정에서 `loginProcessingUrl("/login")`가 있다.
 * login 요청이 오면 자동으로 UserDetailsService 타입으로 loc 되어 있는 loadUserByUsername 함수가 샐행된다.
 */
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 일반 로그인 처리
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmailAndProvider(email, Constants.SYSTEM_STRING);

        if (member != null)
            return new PrincipalDetails(member);

        return null;
    }
}
