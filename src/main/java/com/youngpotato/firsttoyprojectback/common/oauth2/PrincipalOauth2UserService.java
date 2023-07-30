package com.youngpotato.firsttoyprojectback.common.oauth2;

import com.youngpotato.firsttoyprojectback.common.auth.PrincipalDetails;
import com.youngpotato.firsttoyprojectback.common.auth.provider.GoogleUserInfo;
import com.youngpotato.firsttoyprojectback.common.auth.provider.KakaoUserInfo;
import com.youngpotato.firsttoyprojectback.common.auth.provider.NaverUserInfo;
import com.youngpotato.firsttoyprojectback.common.auth.provider.OAuth2UserInfo;
import com.youngpotato.firsttoyprojectback.domain.member.Member;
import com.youngpotato.firsttoyprojectback.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 정상적인 유저 인증이 완료되면 -> 여기로 오게됨 그 다음에 successhandler로 감
 */
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final MemberService memberService;

    /**
     * OAuth2 로그인 처리 (자동 회원가입)
     * Provider(구글, 네이버 등)로 부터 받은 userRequest 데이터에 대한 후처리가 진행되는 메소드
     *
     * 소셜 로그인 클릭 > 소셜 로그인창 > 로그인 완료 > code 리턴 (OAuth-Client 라이브러리가 받음)
     * > AccessToken Request > userRequest 정보 > loadUser() 메소드 호출 > Provider한테 회원 프로필 받음
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // OAuth 로그인 + 자동 회원가입 OR 로그인
        OAuth2UserInfo oAuth2UserInfo = null;

        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
        }

        Member member = memberService.oauthSignup(oAuth2UserInfo);

        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }
}
