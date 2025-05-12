package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.UserGetProfileResponseDto;
import io.powerrangers.backend.dto.UserUpdateProfileRequestDto;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.exception.CustomException;
import io.powerrangers.backend.exception.ErrorCode;
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
        return userRepository.findUserByNickname(nickname).isPresent();
    }

    @Transactional(readOnly = true)
    public UserGetProfileResponseDto getUserProfile(Long userId){
        User findUser =
                userRepository.findUserById(userId)
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
                userRepository.findUserByNickname(nickname)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
        User user = userRepository.findUserById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        userRepository.deleteById(userId);
    }

}
