package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.TokenRepository;
import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.Role;
import io.powerrangers.backend.dto.TaskResponseDto;
import io.powerrangers.backend.dto.TokenBody;
import io.powerrangers.backend.dto.UserGetProfileResponseDto;
import io.powerrangers.backend.dto.UserUpdateProfileRequestDto;
import io.powerrangers.backend.entity.RefreshToken;
import io.powerrangers.backend.entity.Task;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.exception.AuthTokenException;
import io.powerrangers.backend.exception.CustomException;
import io.powerrangers.backend.exception.ErrorCode;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository refreshTokenRepositoryAdapter;
    private final JwtProvider jwtProvider;

    
    private final S3Service s3Service;
    private final TaskService taskService;


    @Transactional(readOnly = true)
    public boolean checkNicknameDuplication(String nickname){
        return userRepository.existsByNickname(nickname);
    }

    public boolean identified(Long userId) {
        return ContextUtil.getCurrentUserId().equals(userId);
    }

    @Transactional(readOnly = true)
    public UserGetProfileResponseDto getUserProfile(Long userId){
        User findUser =
                userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserGetProfileResponseDto userGetProfileResponseDto = UserGetProfileResponseDto.from(findUser);

        return userGetProfileResponseDto;
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByUser(Long userId, LocalDate date) {
        List<Task> tasks = taskService.getTasksByScope(userId);

        return tasks.stream()
                .filter(task -> task.getDueDate().toLocalDate().equals(date))
                .map(task -> TaskResponseDto.from(task))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserGetProfileResponseDto> searchUserProfile(String nickname){
        List<User> userList = userRepository.findByNickname(nickname.trim());

        return userList.stream()
                .map(user -> UserGetProfileResponseDto.from(user))
                .toList();


    }

    @Transactional
    public void updateUserProfile(Long userId, UserUpdateProfileRequestDto request, MultipartFile image) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!identified(userId)){
            throw new CustomException(ErrorCode.NOT_THE_OWNER);
        }

        if(!user.getNickname().equals(request.getNickname()) && checkNicknameDuplication(request.getNickname())){
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        }

        user.setNickname(request.getNickname());
        user.setIntro(request.getIntro());
        updateUserProfileImage(user, image, request.getProfileImage());
    }
    private void updateUserProfileImage(User user, MultipartFile image, String profileImage) throws IOException {
        String existingImageUrl = user.getProfileImage();

        // 1. 이미지 변경 없음 + 유지
        if ((image == null || image.isEmpty()) && !profileImage.isBlank() && profileImage != null ) {
            // 이미지 유지
            return;
        }

        // 2. 이미지 삭제 요청
        if ((image == null || image.isEmpty()) && (profileImage == null || profileImage.isBlank())) {
            if (existingImageUrl != null && !existingImageUrl.isBlank()) {
                s3Service.delete(existingImageUrl);
            }
            user.setProfileImage(null);
            return;
        }

        // 3. 새 이미지 업로드
        if (image != null && !image.isEmpty()) {
            if (!image.getContentType().startsWith("image/")) {
                throw new CustomException(ErrorCode.UNSUPPORTED_RESOURCE);
            }

            if (existingImageUrl != null && !existingImageUrl.isBlank()) {
                s3Service.delete(existingImageUrl);
            }

            String uploadedImageUrl = s3Service.upload(image);
            user.setProfileImage(uploadedImageUrl);
        }
    }

    @Transactional
    public void cancelAccount(Long userId){
        // 계정 주인인지 확인
        if(!identified(userId)){
            throw new CustomException(ErrorCode.NOT_THE_OWNER);
        }
        // 존재하는 계정인가
        userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // 사용자의 리프레시 토큰 블랙리스트에 추가
        findRefreshTokenAndAddToBlackList(userId);
        // 사용자의 refreshToken -> User에 null 입력
        List<RefreshToken> refreshTokens = refreshTokenRepositoryAdapter.findAllRefreshTokensByUserId(userId);
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
        findRefreshTokenAndAddToBlackList(userId);
    }

    private void findRefreshTokenAndAddToBlackList(Long userId) {
        RefreshToken refreshToken = refreshTokenRepositoryAdapter.findValidRefreshToken(userId)
                .orElseThrow(() -> new AuthTokenException(ErrorCode.UNAUTHORIZED));

        String refreshTokenValue = refreshToken.getRefreshToken();

        if(!refreshTokenRepositoryAdapter.tokenBlackList(refreshTokenValue)){
            refreshTokenRepositoryAdapter.addBlackList(refreshToken);
        }
    }

    @Transactional(readOnly = true)
    public String reissueAccessToken(String refreshTokenValue){
        if(!jwtProvider.validateToken(refreshTokenValue)){
            throw new AuthTokenException(ErrorCode.UNAUTHORIZED);
        }

        TokenBody tokenBody = jwtProvider.parseToken(refreshTokenValue);
        Long userId = tokenBody.getUserId();
        Role role = Role.valueOf(tokenBody.getRole());

        RefreshToken refreshToken = refreshTokenRepositoryAdapter.findValidRefreshToken(userId)
                .orElseThrow(() -> new AuthTokenException(ErrorCode.UNAUTHORIZED));

        if(!refreshTokenValue.equals(refreshToken.getRefreshToken())){
            throw new AuthTokenException(ErrorCode.UNAUTHORIZED);
        }
        String reissueAccessToken = jwtProvider.issueAccessToken(userId, role);
        return reissueAccessToken;
    }
}
