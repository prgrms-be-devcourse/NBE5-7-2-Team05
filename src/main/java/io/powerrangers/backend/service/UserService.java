package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.UserGetProfileResponseDto;
import io.powerrangers.backend.dto.UserUpdateProfileRequestDto;
import io.powerrangers.backend.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: 본인 닉네임 중복 제외 로직 추가

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public boolean checkNicknameDuplication(String nickname){
        return userRepository.findUserByNickname(nickname).isPresent();
    }

    @Transactional(readOnly = true)
    public UserGetProfileResponseDto getUserProfile(Long userId){
        User findUser =
                userRepository.findUserById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

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
                userRepository.findUserByNickname(nickname)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserGetProfileResponseDto userGetProfileResponseDto = UserGetProfileResponseDto.builder()
                .nickname(findUser.getNickname())
                .intro(findUser.getIntro())
                .profileImage(findUser.getProfileImage())
                .build();

        return userGetProfileResponseDto;
    }

    // user 로그아웃
    @Transactional
    public void logout(String accessToken){
        /*
        tokenBlacklistService.blacklist(accessToken);
         */
    }

    @Transactional
    public void updateUserProfile(Long userId, UserUpdateProfileRequestDto request){
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));



        if(checkNicknameDuplication(request.getNickname())){
            throw new IllegalArgumentException("닉네임이 중복됩니다.");
        }

        user.changeNickname(request.getNickname());
        user.changeIntro(request.getIntro());
        user.changeProfileImage(request.getProfileImage());
    }

    @Transactional
    public void cancelAccount(Long userId){
        User user = userRepository.findUserById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        userRepository.deleteUserById(userId);
    }

}
