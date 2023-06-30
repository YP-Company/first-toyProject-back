package com.youngpotato.firsttoyprojectback.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.youngpotato.firsttoyprojectback.auth.PrincipalDetails;
import com.youngpotato.firsttoyprojectback.domain.member.Member;
import com.youngpotato.firsttoyprojectback.domain.member.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

/**
 * security가 filter을 가지고 있는데 그 필터 중에 BasicAuthenticationFilter가 있음
 * 권한이나 특정 주소를 요청했을 때 위 필터를 무조건 타게 되어있다.
 * 만약 권한 or 인증이 필요한 주소가 아니라면 해당 필터를 안탄다.
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final MemberRepository memberRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(JwtConstants.HEADER_STRING);

        if (header == null || !header.startsWith(JwtConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        String token = request.getHeader(JwtConstants.HEADER_STRING)
                .replace(JwtConstants.TOKEN_PREFIX, "");

        /**
         * 토근 검증 (이게 인증이기 때문에 AUthenticationManager도 필요 없음)
         * 내가 SecurityContext에 직접 접근해서 세션을 만들 때 자동으로 UserDetailsService에 있는 loadByUsername()이 호출됨
         */
        String username = JWT
                .require(Algorithm.HMAC512(JwtConstants.SECRET))
                .build()
                .verify(token)
                .getClaim("username")
                .asString();

        if (username != null) {
            Member member = memberRepository.findByUsername(username);

            /**
             * 인증은 토근 검증시 끝.
             * 인증을 하기 위해서가 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해
             * 아래와 같이 토근을 생성하여 Authetication 객첼르 강제로 만들고 그걸 세션에 저장.
             */
            PrincipalDetails principalDetails = new PrincipalDetails(member);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails, // 나중에 컨트롤러에에서 DI해서 쓸 때 사용하기 간편
                    null, // 패스워드는 모르니까 null처리, 어차피 지금 인증하는 것이 아니니까
                    principalDetails.getAuthorities()
            );

            /** 강체로 시큐리티의 세션에 접근하여 값 저장 */
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
