package io.powerrangers.backend.service;

import io.powerrangers.backend.dto.UserDetails;
import io.powerrangers.backend.exception.AuthTokenException;
import io.powerrangers.backend.exception.CustomException;
import io.powerrangers.backend.exception.ErrorCode;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Slf4j
@SuppressWarnings("unchecked")
public class UserDetailsFactory {
    public static UserDetails userDetails(OAuth2User oAuth2User, String providerId) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        switch (providerId.toLowerCase()) {
            case "google" -> {
                return UserDetails.builder()
                        .name(attributes.get("name").toString())
                        .email(attributes.get("email").toString())
                        .providerId(attributes.get("sub").toString())
                        .profileImage(attributes.get("picture").toString())
                        .attributes(attributes)
                        .build();
            }
            case "kakao" -> {
                Map<String, String> properties = (Map<String, String>) attributes.get("properties");
                return UserDetails.builder()
                        .name(properties.get("nickname"))
                        .email(attributes.get("id").toString() + "@kakao.com")
                        .providerId(attributes.get("id").toString())
                        .profileImage(properties.get("profile_image"))
                        .attributes(attributes)
                        .build();
            }
            case "naver" -> {
                Map<String, String> response = (Map<String, String>) attributes.get("response");
                return UserDetails.builder()
                        .name(response.get("name"))
                        .email(response.get("email"))
                        .providerId(response.get("id"))
                        .profileImage(response.get("profile_image"))
                        .attributes(attributes)
                        .build();
            }
            default -> {
                log.warn("[인증 실패] 지원하지 않는 providerId: {}",providerId);
                throw new AuthTokenException(ErrorCode.UNAUTHORIZED);
            }
        }
    }
}
