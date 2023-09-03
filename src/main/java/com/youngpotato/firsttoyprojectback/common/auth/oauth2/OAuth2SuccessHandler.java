package com.youngpotato.firsttoyprojectback.common.auth.oauth2;

import com.youngpotato.firsttoyprojectback.common.Constants;
import com.youngpotato.firsttoyprojectback.common.jwt.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    /**
     * 소셜 로그인 성공시 jwt 생성하여 헤더에 주입
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String jwt = tokenProvider.createToken(authentication);
        response.addHeader(Constants.JWT_HEADER_STRING, Constants.JWT_TOKEN_PREFIX + jwt);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
