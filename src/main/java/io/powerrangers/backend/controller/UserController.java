package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.BaseResponse;
import io.powerrangers.backend.dto.SuccessCode;
import io.powerrangers.backend.dto.LogoutRequestDto;
import io.powerrangers.backend.dto.UserGetProfileResponseDto;
import io.powerrangers.backend.dto.UserUpdateProfileRequestDto;
import io.powerrangers.backend.service.UserService;
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
        return BaseResponse.ok(SuccessCode.MODIFIED_SUCCESS);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<UserGetProfileResponseDto>> getUserProfile(@PathVariable Long userId){
        return BaseResponse.ok(SuccessCode.GET_SUCCESS, userService.getUserProfile(userId));
    }

    @GetMapping()
    public ResponseEntity<BaseResponse<UserGetProfileResponseDto>> searchUserProfile(@RequestParam String nickname){
        return BaseResponse.ok(SuccessCode.GET_SUCCESS, userService.searchUserProfile(nickname));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<BaseResponse<?>> cancelAccount(@PathVariable Long userId){
        userService.cancelAccount(userId);
        return BaseResponse.ok(SuccessCode.DELETED_SUCCESS);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(){
        userService.logout();
        return ResponseEntity.ok().build();
    }
}
