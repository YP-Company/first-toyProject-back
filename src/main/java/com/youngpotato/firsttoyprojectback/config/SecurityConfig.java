package com.youngpotato.firsttoyprojectback.config;

import com.youngpotato.firsttoyprojectback.common.jwt.*;
import com.youngpotato.firsttoyprojectback.domain.member.MemberRepository;
import com.youngpotato.firsttoyprojectback.common.oauth2.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됨.
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

//    private final MemberRepository memberRepository;
//    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // StateLess한 rest api를 개발할 것이므로 csrf 공격에 대한 옵션은 끈다.
//                .addFilter(corsConfig.corsFilter())
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic().disable()
                .formLogin().disable(); // 스프링 시큐리티에서 제공하는 로그인 페이지를 안쓰기 위해

        /**
         * 서버는 session 메모리 영역이 존재
         * 로그인 진행 > 서버는 session id를 response한다.
         * session id를 받은 클라이언트는 보통 cookie에 해당 값을 저장한다.
         * 이 후 새로운 resquest가 발생하면 쿠키에 있는 session id를 서버에 같이 보낸다.
         * 서버별로 세션 영역이 나뉘어져 있기 때문에 해당 방식은 서버가 여러대일 경우 좋지 않다.
         * JWT 방식은 세션을 사용하지 않기 때문에 STATELESS로 설정
         */
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        /** 에러 방지 */
        http
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler);

        /** 권한 설정 */
        http.authorizeRequests()
                .requestMatchers("/api/v1/user/**").authenticated()
                .requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")
                .anyRequest().permitAll();

        /**
         * JWT를 위한 Filter를 아래에서 만들어 줄건데,
         * 이 Filter를 어느위치에서 사용하겠다고 등록을 해주어야 Filter가 작동이 됩니다.
         * security 로직에 JwtFilter 등록
         * .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
         */
        http
                .apply(new JwtSecurityConfig(tokenProvider));

//        /**
//         * Header에 Authorization을 담고 통신하는 방법 2가지
//         * 1. Basic 방식
//         * id, pw를 담고 request를 보내기 때문에 보안에 취약하다고 한다.
//         * http 방식 -> id, pw 암호화 안된 상태로 통신
//         * https 방식 -> id, pw 암호화 된 상태로 통신
//         * 2. Bearer 방식
//         * id, pw로 생성된 token(jwt)을 담고 request를 보내기에 보안성이 위 방법보다는 좋다고 한다.
//         * 해당 토큰은 노출이 되어도 괜찮다.
//         * 물론 노출이 안되는게 좋다. 해당 토큰을 통해 로그인이 가능하다.
//         * 하지만 토큰의 유효시간이 있기 때문에 안전하다. 위 방법보다는 안전하다고 한다.
//         */
//        http
//                // JWT을 위한 Filter를 아래에서 만들어 줄건데,
//                // 이 Filter를 어느위치에서 사용하겠다고 등록을 해주어야 Filter가 작동이 됩니다.
//                // security 로직에 JwtFilter 등록
//                // .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
//                .apply(new MyCustomDsl());
//
//        // 에러 방지
////        http
////                .exceptionHandling()
////                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
////                .accessDeniedHandler(new JwtAccessDeniedHandler());
//
//        // oauth2
//        http.oauth2Login()
//                .authorizationEndpoint()
//                .baseUri("/oauth2/authorization") // 소셜 로그인 요청을 보내는 url을 설정
//                .and()
//                .redirectionEndpoint()
//                .baseUri("/login/oauth2/code/*") // 소셜 인증 후 redirect 되는 uri
//                .and()
//                .userInfoEndpoint()
//                .userService(principalOauth2UserService) // 회원 정보를 처리하기 위한 클래스 설정
//                .and()
//                .defaultSuccessUrl("/success-oauth");
////                .successHandler() // oauth 인증 성공 시 호출되는 handler
////                .failureHandler(); // oauth 인증 실패 시 호출되는 handler
////                .authorizationRequestRepository()

        return http.build();
    }

//    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
//
//        @Override
//        public void configure(HttpSecurity http) {
//            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
//
//            http
//                    .addFilter(corsConfig.corsFilter())
//                    .addFilter(new JwtAuthenticationFilter(authenticationManager))
//                    .addFilter(new JwtAuthorizationFilter(authenticationManager, memberRepository));
//        }
//    }
}
