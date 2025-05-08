package io.powerrangers.backend.controller;

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
    public ResponseEntity<?> updateUserProfile(@PathVariable Long userId, @RequestBody UserUpdateProfileRequestDto request){
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId){
        return ResponseEntity.ok().build();
    }



    @GetMapping()
    public ResponseEntity<?> searchUserProfile(@RequestParam String nickname){
        return ResponseEntity.ok().build();
    }

}
