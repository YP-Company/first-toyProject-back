package com.youngpotato.firsttoyprojectback.common.auth.oauth2;

import com.youngpotato.firsttoyprojectback.common.auth.PrincipalDetails;
import com.youngpotato.firsttoyprojectback.common.auth.oauth2.provider.GoogleUserInfo;
import com.youngpotato.firsttoyprojectback.common.auth.oauth2.provider.KakaoUserInfo;
import com.youngpotato.firsttoyprojectback.common.auth.oauth2.provider.NaverUserInfo;
import com.youngpotato.firsttoyprojectback.common.auth.oauth2.provider.OAuth2UserInfo;
import com.youngpotato.firsttoyprojectback.domain.member.Member;
import com.youngpotato.firsttoyprojectback.domain.member.MemberRepository;
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

    private final MemberRepository memberRepository;

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

        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
        }

        String email = oAuth2UserInfo.getEmail();
        String password = "password"; // 무의미 값
        String nickName = oAuth2UserInfo.getName();
        String role = "ROLE_USER";
        String provider = oAuth2UserInfo.getProvider(); // 플랫폼 명
        String providerId = oAuth2UserInfo.getProviderId(); // 플랫품 id

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

        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }
}
