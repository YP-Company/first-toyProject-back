package com.youngpotato.firsttoyprojectback.common.auth.oauth2.provider;

public interface OAuth2UserInfo {

    String getProviderId();

    String getProvider();

    String getEmail();

    String getName();
}
