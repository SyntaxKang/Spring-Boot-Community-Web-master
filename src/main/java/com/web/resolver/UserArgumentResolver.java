package com.web.resolver;


import com.web.annotation.SocialUser;
import com.web.domain.User;
import com.web.domain.enums.SocialType;
import com.web.repository.UserRepository;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.web.domain.enums.SocialType.*;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private UserRepository userRepository;

    public UserArgumentResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(SocialUser.class) != null && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        User user = (User) session.getAttribute("user");
        return getUser(user, session);
    }

    private User getUser(User user, HttpSession session) {
        if(user == null) {
            try {
                OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
                Map<String, Object> map = authentication.getPrincipal().getAttributes();

                //인증된 소셜미디어가 무엇인지 판단
                User convertUser = convertUser(authentication.getAuthorizedClientRegistrationId(), map);


                user = userRepository.findByEmail(convertUser.getEmail());
                if (user == null) { user = userRepository.save(convertUser); }

                setRoleIfNotSame(user, authentication, map);
                session.setAttribute("user", user);
            } catch (ClassCastException e) {
                return user;
            }
        }
        return user;
    }

    private User convertUser(String authority, Map<String, Object> map) {
        //if(FACEBOOK.isEquals(authority)) return getModernUser(FACEBOOK, map);
         if(GOOGLE.isEquals(authority)) return getModernUser(GOOGLE, map);
        else if(KAKAO.isEquals(authority)) return getKaKaoUser(map);
        else if(NAVER.isEquals(authority)) return getNaverUser(map);
        return null;
    }

    private User getModernUser(SocialType socialType, Map<String, Object> map) {
        return User.builder()
                .name(String.valueOf(map.get("name")))
                .email(String.valueOf(map.get("email")))
                .pincipal(String.valueOf(map.get("id")))
                .socialType(socialType)
                .createdDate(LocalDateTime.now())
                .build();
    }

    private User getKaKaoUser(Map<String, Object> map) {
        System.out.println(map);
        Map<String, String> propertyMap = (HashMap<String, String>) map.get("properties");
        System.out.println(propertyMap);
        System.out.println(map.get("kakao_account"));
        Map<String, String> propertyMap2 = (HashMap<String, String>) map.get("kakao_account");
        System.out.println("이메일아 나와라"+propertyMap2.get("email"));
        return User.builder()
                .name(propertyMap.get("nickname"))
                .email(propertyMap2.get("email"))
                .pincipal(String.valueOf(map.get("id")))
                .socialType(KAKAO)
                .createdDate(LocalDateTime.now())
                .build();

    }

    private User getNaverUser(Map<String, Object> map) {
        Map<String, String> propertyMap = (HashMap<String, String>) map.get("response");
        System.out.println("map나와라"+propertyMap);
        System.out.println("이름나와라"+propertyMap.get("name"));
        System.out.println("메일나와라"+propertyMap.get("email"));
        return User.builder()
                .name(propertyMap.get("name"))
                .email((propertyMap.get("email")))
                .pincipal(String.valueOf(map.get("id")))
                .socialType(NAVER)
                .createdDate(LocalDateTime.now())
                .build();
    }


    private void setRoleIfNotSame(User user, OAuth2AuthenticationToken authentication, Map<String, Object> map) {
        if(!authentication.getAuthorities().contains(new SimpleGrantedAuthority(user.getSocialType().getRoleType()))) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(map, "N/A", AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())));
        }
    }
}