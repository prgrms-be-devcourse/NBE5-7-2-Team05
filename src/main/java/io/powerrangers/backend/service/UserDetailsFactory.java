package io.powerrangers.backend.service;

import io.powerrangers.backend.dto.UserDetails;
import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;

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
            default -> throw new IllegalArgumentException("Unsupported provider: " + providerId);
        }
    }
}
