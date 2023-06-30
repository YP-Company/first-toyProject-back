package com.youngpotato.firsttoyprojectback.config;

import com.youngpotato.firsttoyprojectback.domain.member.MemberRepository;
import com.youngpotato.firsttoyprojectback.jwt.JwtAuthenticationFilter;
import com.youngpotato.firsttoyprojectback.jwt.JwtAuthorizationFilter;
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

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됨.
public class SecurityConfig {

    private final CorsConfig corsConfig;

    private final MemberRepository memberRepository;

    public SecurityConfig(CorsConfig corsConfig, MemberRepository memberRepository) {
        this.corsConfig = corsConfig;
        this.memberRepository = memberRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // stateless한 rest api를 개발할 것이므로 csrf 공격에 대한 옵션은 꺼둔다.
//                .csrf().disable()
                .csrf(AbstractHttpConfigurer::disable)

                /**
                 * 서버는 session 메모리 영역이 존재
                 * 로그인 진행 > 서버는 session id를 response한다.
                 * session id를 받은 클라이언트는 보통 cookie에 해당 값을 저장한다.
                 * 이 후 새로운 resquest가 발생하면 쿠키에 있는 session id를 서버에 같이 보낸다.
                 * 서버별로 세션 영역이 나뉘어져 있기 때문에 해당 방식은 서버가 여러대일 경우 좋지 않다.
                 * 세션을 사용하지 않기 때문에 STATELESS로 설정
                 */
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionManagement((httpSecuritySessionManagementConfigurer) ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

//                .formLogin().disable()
                .formLogin(AbstractHttpConfigurer::disable)

                /**
                 * Header에 Authorization을 담고 통신하는 방법 2가지
                 * 1. Basic 방식
                 * id, pw를 담고 request를 보내기 때문에 보안에 취약하다고 한다.
                 * http 방식 -> id, pw 암호화 안된 상태로 통신
                 * https 방식 -> id, pw 암호화 된 상태로 통신
                 * 2. Bearer 방식
                 * id, pw로 생성된 token(jwt)을 담고 request를 보내기에 보안성이 위 방법보다는 좋다고 한다.
                 * 해당 토큰은 노출이 되어도 괜찮다.
                 * 물론 노출이 안되는게 좋다. 해당 토큰을 통해 로그인이 가능하다.
                 * 하지만 토큰의 유효시간이 있기 때문에 안전하다. 위 방법보다는 안전하다고 한다.
                 */
//                .httpBasic().disable()
                .httpBasic(AbstractHttpConfigurer::disable)

                .apply(new MyCustomDsl())
                .and()

                // 특정 URL에 대한 권한 설정.
//                .authorizeRequests()
//                .antMatchers("/api/v1/user/**")
//                .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//                .antMatchers("/api/v1/manager/**")
//                .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//                .antMatchers("/api/v1/admin/**")
//                .access("hasRole('ROLE_ADMIN')")
//                .anyRequest().permitAll();
                .authorizeHttpRequests((authorizationManagerRequestMatcherRegistry -> {
                    authorizationManagerRequestMatcherRegistry.requestMatchers("/api/v1/user/**")
                            .authenticated();

                    authorizationManagerRequestMatcherRegistry.requestMatchers("/api/v1/manager/**")
                            // ROLE_은 붙이면 안 된다. hasAnyRole()을 사용할 때 자동으로 ROLE_이 붙기 때문.
                            .hasAnyRole("ADMIN", "MANAGER");

                    authorizationManagerRequestMatcherRegistry.requestMatchers("/api/v1/admin/**")
                            .hasAnyRole("ADMIN");

                    authorizationManagerRequestMatcherRegistry.anyRequest().permitAll();
                }))

                .build();

    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);

            http
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, memberRepository));
        }
    }
}
