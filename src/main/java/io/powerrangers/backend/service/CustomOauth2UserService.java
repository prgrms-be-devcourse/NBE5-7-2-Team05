package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.UserDetails;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.exception.AuthTokenException;
import io.powerrangers.backend.exception.CustomException;
import io.powerrangers.backend.exception.ErrorCode;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public UserDetails getUserDetails(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AuthTokenException(ErrorCode.USER_NOT_FOUND));
        return UserDetails.from(user);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, naver, kakao
        UserDetails userDetails = UserDetailsFactory.userDetails(oAuth2User, provider);
        String tempNickname = userDetails.getName();

        Optional<User> optionalUser = userRepository.findByEmail(userDetails.getEmail());
        User findUser = null;

        if (optionalUser.isEmpty()) {
            while(userRepository.existsByNickname(tempNickname)) {
                tempNickname = generateTempNickname(tempNickname);
            }
            findUser = User.builder()
                    .nickname(tempNickname)
                    .email(userDetails.getEmail())
                    .provider(provider)
                    .providerId(userDetails.getProviderId())
                    .profileImage(userDetails.getProfileImage())
                    .build();
            findUser = userRepository.save(findUser);
        } else {
            findUser = optionalUser.get();
        }

        if (!findUser.getProvider().equals(provider)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("already_signed_in", "이미 다른 제공자로 로그인한 적이 있습니다.", null)
            );
        }

        log.info("userDetails = {}", userDetails);
        return userDetails.setId(findUser.getId()).setRole(findUser.getRole());
    }

    private String generateTempNickname(String nickname) {
        String[] adjectives = {"귀여운 ", "멋진 ", "행복한 ", "용감한 "};
        String[] powerRangers = {"레드 ", "블루 ", "옐로우 ", "그린 ", "핑크 "};

        String adj = adjectives[new Random().nextInt(adjectives.length)];
        String noun = powerRangers[new Random().nextInt(powerRangers.length)];
        int number = new Random().nextInt(1000);

        return adj + noun + nickname + number;  // 귀여운 레드 홍길동123
    }
}
