package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.BaseResponse;
import io.powerrangers.backend.dto.SuccessCode;
import io.powerrangers.backend.dto.TaskResponseDto;
import io.powerrangers.backend.dto.UserGetProfileResponseDto;
import io.powerrangers.backend.dto.UserUpdateProfileRequestDto;
import io.powerrangers.backend.service.CookieFactory;
import io.powerrangers.backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PatchMapping(value ="/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<?>> updateUserProfile(
            @PathVariable Long userId,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "dto") UserUpdateProfileRequestDto request
            ){
        log.info("nickname = " + request.getNickname());
        log.info("intro = " + request.getIntro());
        userService.updateUserProfile(userId, request, image);
        return BaseResponse.success(SuccessCode.MODIFIED_SUCCESS);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<UserGetProfileResponseDto>> getUserProfile(@PathVariable Long userId){
        return BaseResponse.success(SuccessCode.GET_SUCCESS, userService.getUserProfile(userId));
    }

    @GetMapping()
    public ResponseEntity<BaseResponse<List<UserGetProfileResponseDto>>> searchUserProfile(@RequestParam String nickname){
        return BaseResponse.success(SuccessCode.GET_SUCCESS, userService.searchUserProfile(nickname));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<BaseResponse<?>> cancelAccount(@PathVariable Long userId){
        userService.cancelAccount(userId);
        return BaseResponse.success(SuccessCode.DELETED_SUCCESS);
    }

    @GetMapping("/{userId}/tasks")
    public ResponseEntity<BaseResponse<List<TaskResponseDto>>> getUserTasks(@PathVariable Long userId, @RequestParam LocalDate date) {
        return BaseResponse.success(SuccessCode.GET_SUCCESS, userService.getTasksByUser(userId, date));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(){
        userService.logout();
        ResponseCookie deleteAccessCookie = CookieFactory.deleteAccessCookie();
        ResponseCookie deleteRefreshCookie = CookieFactory.deleteRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString())
                .build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<String> reissueToken(
            @CookieValue(value = CookieFactory.REFRESH_TOKEN) String refreshToken
    ){
        String newAccessToken = userService.reissueAccessToken(refreshToken);
        ResponseCookie accessCookie = CookieFactory.createAccessCookie(newAccessToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .build();
    }
}
