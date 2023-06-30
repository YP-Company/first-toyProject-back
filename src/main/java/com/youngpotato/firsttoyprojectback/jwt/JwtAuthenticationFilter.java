package com.youngpotato.firsttoyprojectback.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youngpotato.firsttoyprojectback.auth.PrincipalDetails;
import com.youngpotato.firsttoyprojectback.web.dto.LoginDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Date;

/**
 * Spring Security에서 UsernamePasswordAuthenticationFilter가 있음
 * "/login" 요청해서 username, password를 request(POST)하면 해당 필터가 동작한다.
 * SecurityConfig에 formLogin을 disable했기 때문에 실행이 안되지만
 * SecurityConfig에 직접 .addFilter(new JwtAuthenticationFilter())로 해당 필터로 등록한다.
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Authentication 객체 만들어서 리턴 -> 의존 : AuthenticationManager
     * "/login" 요청 시 로그인 시도를 위해 실행되는 함수
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        /**
         * 1. username, password를 받아서
         * 2. 정상적인 로그인인지 실행하면. authenticationManager로 로그인 시도..?
         * PrincipalDetailsService가 호출 loadUserByUsername() 함수 실행
         * 3. PrincipalDetails를 세션에 담고 (세션에 담는 이유는 권한 관리 때문)
         * 4. jwt 토큰을 만들어서 응답
         */

        // request에 있는 username, password를 파싱하여 Object로 받기
        ObjectMapper om = new ObjectMapper();
        LoginDTO loginDto = null;
        try {
            loginDto = om.readValue(request.getInputStream(), LoginDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        /**
         * authenticate() 함수가 호출 되면 인증 프로바이더가 유저 디테일 서비스의
         * loadUserByUsername(토큰의 첫번째 파라메터)를 호출하고
         * UserDetails를 리턴받아서 토큰의 두번째 파라메터(credential)과
         * UserDetails(DB값)의 getPassword()함수로 비교해서 동일하면
         * Authentication 객체를 만들어서 필터체인으로 리턴해준다.
         */

        /**
         * PrincipalDetailsService의 loadUserByUsername() 실행됨
         * 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
         * Tip: 인증 프로바이더의 디폴트 암호화 방식은 BCryptPasswordEncoder
         * 결론은 인증 프로바이더에게 알려줄 필요가 없음.
         */
        return authenticationManager.authenticate(authenticationToken);
    }

    /**
     * attemptAuthentication() 실행 후 인증이 정상적으로 되었으면 해당 함수가 실행됨
     * JWT Token 생성해서 response에 담아주기
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        PrincipalDetails principalDetailis = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject(principalDetailis.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtConstants.EXPIRATION_TIME))
                .withClaim("id", principalDetailis.getMember().getMemberId())
                .withClaim("username", principalDetailis.getMember().getUsername())
                .sign(Algorithm.HMAC512(JwtConstants.SECRET));

        response.addHeader(JwtConstants.HEADER_STRING, JwtConstants.TOKEN_PREFIX + jwtToken);
    }
}
