package com.web.oauth;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

public enum CustomOAuth2Provider {

    KAKAO {
        @Override
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId, ClientAuthenticationMethod.POST, DEFAULT_LOGIN_REDIRECT_URL);
            builder.scope("profile","account_email");
            builder.authorizationUri("https://kauth.kakao.com/oauth/authorize");
            builder.tokenUri("https://kauth.kakao.com/oauth/token");
            builder.userInfoUri("https://kapi.kakao.com/v2/user/me");

            builder.userNameAttributeName("id");
            builder.clientName("kakao");
            return builder;
        }
    },
    NAVER {
        @Override
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId, ClientAuthenticationMethod.POST, DEFAULT_LOGIN_REDIRECT_URL);
            builder.scope("");
            //네이버 아이디 로그인 인증 요청
            builder.authorizationUri("https://nid.naver.com/oauth2.0/authorize");
            //접근 토큰 발급/삭제/갱신
            builder.tokenUri("https://nid.naver.com/oauth2.0/token");
            //네이버 회원 프로필 조회
            builder.userInfoUri("https://openapi.naver.com/v1/nid/me");
            builder.userNameAttributeName("resultcode");
            builder.clientName("naver");
            return builder;
        }
    };


    private static final String DEFAULT_LOGIN_REDIRECT_URL = "{baseUrl}/login/oauth2/code/{registrationId}";


    protected final ClientRegistration.Builder getBuilder(String registrationId, ClientAuthenticationMethod method, String redirectUri) {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
        builder.clientAuthenticationMethod(method);
        builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        builder.redirectUriTemplate(redirectUri);
        return builder;
    }



    public abstract ClientRegistration.Builder getBuilder(String registrationId);



}
