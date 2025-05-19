package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.BaseResponse;
import io.powerrangers.backend.dto.FollowCountResponseDto;
import io.powerrangers.backend.dto.FollowRequestDto;
import io.powerrangers.backend.dto.FollowResponseDto;
import io.powerrangers.backend.dto.UserFollowResponseDto;
import io.powerrangers.backend.service.FollowService;
import io.powerrangers.backend.service.ContextUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<BaseResponse<FollowResponseDto>> follow(@Valid @RequestBody FollowRequestDto followRequestDto){
        return BaseResponse.success(HttpStatus.CREATED, followService.follow(followRequestDto));
    }

    @DeleteMapping("/{followingId}")
    public ResponseEntity<BaseResponse<?>> unfollow(@PathVariable Long followingId){
        followService.unfollow(followingId);
        return BaseResponse.success(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<BaseResponse<List<UserFollowResponseDto>>> getFollowers(@PathVariable Long userId){
        return BaseResponse.success(HttpStatus.OK, followService.findFollowers(userId));
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<BaseResponse<List<UserFollowResponseDto>>> getFollowings(@PathVariable Long userId){
        return BaseResponse.success(HttpStatus.OK, followService.findFollowings(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<FollowCountResponseDto>> getFollowCount(@PathVariable Long userId){
        return BaseResponse.success(SuccessCode.GET_SUCCESS, followService.getFollowCount(userId));
    }

}
