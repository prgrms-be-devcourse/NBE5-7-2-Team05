package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.UserGetProfileResponseDto;
import io.powerrangers.backend.dto.UserUpdateProfileRequestDto;
import io.powerrangers.backend.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public boolean checkNicknameDuplication(String nickname){
        return userRepository.findByNickname(nickname).isPresent();
    }

    @Transactional(readOnly = true)
    public UserGetProfileResponseDto getUserProfile(Long userId){
        User findUser =
                userRepository.findById(userId)
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
                userRepository.findByNickname(nickname)
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if(!user.getNickname().equals(request.getNickname()) && checkNicknameDuplication(request.getNickname())){
            throw new IllegalArgumentException("닉네임이 중복됩니다.");
        }

        user.setNickname(request.getNickname());
        user.setIntro(request.getIntro());
        user.setProfileImage(request.getProfileImage());
    }

    @Transactional
    public void cancelAccount(Long userId){
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        userRepository.deleteById(userId);
    }

}
