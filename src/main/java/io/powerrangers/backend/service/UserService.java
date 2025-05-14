package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.RefreshTokenRepository;
import io.powerrangers.backend.dao.TokenRepository;
import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.UserGetProfileResponseDto;
import io.powerrangers.backend.dto.UserUpdateProfileRequestDto;
import io.powerrangers.backend.entity.RefreshToken;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.exception.CustomException;
import io.powerrangers.backend.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository refreshTokenRepositoryAdapter;

    @Transactional(readOnly = true)
    public boolean checkNicknameDuplication(String nickname){
        return userRepository.findByNickname(nickname).isPresent();
    }

    public boolean identified(Long userId) {
        if(ContextUtil.getCurrentUserId().equals(userId)) {
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public UserGetProfileResponseDto getUserProfile(Long userId){
        User findUser =
                userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserGetProfileResponseDto userGetProfileResponseDto = UserGetProfileResponseDto.builder()
                .nickname(findUser.getNickname())
                .intro(findUser.getIntro())
                .profileImage(findUser.getProfileImage())
                .build();

        return userGetProfileResponseDto;
    }

    @Transactional(readOnly = true)
    public UserGetProfileResponseDto searchUserProfile(String nickname){
        User findUser =
                userRepository.findByNickname(nickname)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserGetProfileResponseDto userGetProfileResponseDto = UserGetProfileResponseDto.builder()
                .nickname(findUser.getNickname())
                .intro(findUser.getIntro())
                .profileImage(findUser.getProfileImage())
                .build();

        return userGetProfileResponseDto;
    }

    @Transactional
    public void updateUserProfile(Long userId, UserUpdateProfileRequestDto request){
        if(!identified(userId)){
            new CustomException(ErrorCode.NOT_THE_OWNER);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!user.getNickname().equals(request.getNickname()) && checkNicknameDuplication(request.getNickname())){
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        }

        user.setNickname(request.getNickname());
        user.setIntro(request.getIntro());
        user.setProfileImage(request.getProfileImage());
    }

    @Transactional
    public void cancelAccount(Long userId){
        // 계정 주인인지 확인
        if(!identified(userId)){
            new CustomException(ErrorCode.NOT_THE_OWNER);
        }

        // 존재하는 계정인가
        userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자의 RefreshToken 얻어서 -> BlackList로 (예외 및 로그아웃과 중복되는 코드는 이후 리팩토링 예정.. logout이 최신 버전이 아니라)
        RefreshToken refreshToken = refreshTokenRepositoryAdapter.findValidRefreshToken(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        String refreshTokenValue = refreshToken.getRefreshToken();

        if(!refreshTokenRepositoryAdapter.tokenBlackList(refreshTokenValue)){
            refreshTokenRepositoryAdapter.addBlackList(refreshToken);
        }

        // 사용자의 refreshToken -> User에 null 입력
        List<RefreshToken> refreshTokens = refreshTokenRepositoryAdapter.findAllRefreshTokensByUserId(
                userId);
        for(RefreshToken token : refreshTokens) {
            token.setUser(null);
        }

        userRepository.deleteById(userId);
    }

    // user 로그아웃
    @Transactional
    public void logout(){
        log.info("logout start");
        Long userId = ContextUtil.getCurrentUserId();
        log.info("logout userId = {}", userId);
        RefreshToken refreshToken = refreshTokenRepositoryAdapter.findValidRefreshToken(userId)
                .orElseThrow(() -> new IllegalArgumentException("user id에 해당하는 토큰이 없습니다."));

        String refreshTokenValue = refreshToken.getRefreshToken();
        if(!refreshTokenRepositoryAdapter.tokenBlackList(refreshTokenValue)){
            refreshTokenRepositoryAdapter.addBlackList(refreshToken);
        }
    }
}
