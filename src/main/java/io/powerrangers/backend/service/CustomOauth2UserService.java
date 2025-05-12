package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.UserDetails;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.exception.CustomException;
import io.powerrangers.backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public UserDetails getUserDetails(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserDetails.from(user);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, naver, kakao
        UserDetails userDetails = UserDetailsFactory.userDetails(oAuth2User, provider);

        User findUser = userRepository.findByEmail(userDetails.getEmail()).orElseGet(
                () -> {
                    User user = User.builder()
                            .nickname(userDetails.getName())
                            .email(userDetails.getEmail())
                            .provider(provider)
                            .providerId(userDetails.getProviderId())
                            .profileImage(userDetails.getProfileImage())
                            .build();
                    return userRepository.save(user);
                }
        );

        if (findUser.getProvider().equals(provider)) {
            log.info("userDetails = {}", userDetails);
            return userDetails.setId(findUser.getId()).setRole(findUser.getRole());
        } else {
            throw new OAuth2AuthenticationException("User already registered with another provider.");
        }
    }
}
