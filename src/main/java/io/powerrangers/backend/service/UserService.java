package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.UserGetProfileResponseDto;
import io.powerrangers.backend.dto.UserProfileBaseDto;
import io.powerrangers.backend.dto.UserUpdateProfileRequestDto;
import io.powerrangers.backend.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean checkNicknameDuplication(String nickname){
        return userRepository.findUserByNickname(nickname).isPresent();
    }

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
    public void logout(){}

    public void updateUserProfile(Long userId, UserUpdateProfileRequestDto request){
        User user = userRepository.findUserById((userId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        checkNicknameDuplication(request.getNickname());

        user.changeNickname(request.getNickname());
        user.changeIntro(request.getIntro());
        user.changeProfileImage(request.getProfileImage());
    }

    // user 회원 탈퇴 -> access token 삭제 및 Soft Delete
    public void cancleAccount(){

    }

}
