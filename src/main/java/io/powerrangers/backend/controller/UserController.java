package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.BaseResponse;
import io.powerrangers.backend.dto.SuccessCode;
import io.powerrangers.backend.dto.UserGetProfileResponseDto;
import io.powerrangers.backend.dto.UserUpdateProfileRequestDto;
import io.powerrangers.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PatchMapping("/{userId}")
    public ResponseEntity<BaseResponse<?>> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserUpdateProfileRequestDto request
    ){
        userService.updateUserProfile(userId, request);
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(){
        userService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<String> reissueToken(
            @RequestHeader("Authorization") String refreshToken
            ,HttpServletResponse response
    ){
        String newAccessToken = userService.reissueAccessToken(refreshToken);
        response.setHeader("Authorization", "Bearer " + newAccessToken);
        return ResponseEntity.ok().build();
    }
}
