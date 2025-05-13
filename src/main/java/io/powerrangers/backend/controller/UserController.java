package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.LogoutRequestDto;
import io.powerrangers.backend.dto.UserGetProfileResponseDto;
import io.powerrangers.backend.dto.UserUpdateProfileRequestDto;
import io.powerrangers.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PatchMapping("/{userId}")
    public ResponseEntity<UserUpdateProfileRequestDto> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserUpdateProfileRequestDto request
    ){
        userService.updateUserProfile(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserGetProfileResponseDto> getUserProfile(@PathVariable Long userId){

        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @GetMapping()
    public ResponseEntity<UserGetProfileResponseDto> searchUserProfile(@RequestParam String nickname){
        return ResponseEntity.ok(userService.searchUserProfile(nickname));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> cancelAccount(@PathVariable Long userId){
        userService.cancelAccount(userId);
        return ResponseEntity.ok().build();
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
