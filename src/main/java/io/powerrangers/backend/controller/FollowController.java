package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.BaseResponse;
import io.powerrangers.backend.dto.FollowRequestDto;
import io.powerrangers.backend.dto.FollowResponseDto;
import io.powerrangers.backend.dto.SuccessCode;
import io.powerrangers.backend.dto.UserFollowResponseDto;
import io.powerrangers.backend.entity.Follow;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.service.FollowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // TODO : 이후 Authentication에서 ID 받아오기
    @PostMapping
    public ResponseEntity<BaseResponse<FollowResponseDto>> follow(@RequestBody FollowRequestDto followRequestDto){
        return BaseResponse.ok(SuccessCode.ADDED_SUCCESS, followService.follow(followRequestDto));
    }

    @DeleteMapping("/{followingId}")
    public ResponseEntity<BaseResponse<?>> unfollow(@PathVariable Long followingId){
        followService.unfollow(followingId);
        return BaseResponse.ok(SuccessCode.DELETED_SUCCESS);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<BaseResponse<List<UserFollowResponseDto>>> getFollowers(@PathVariable Long userId){
        return BaseResponse.ok(SuccessCode.GET_SUCCESS, followService.findFollowers(userId));
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<BaseResponse<List<UserFollowResponseDto>>> getFollowings(@PathVariable Long userId){
        return BaseResponse.ok(SuccessCode.GET_SUCCESS, followService.findFollowings(userId));
    }

}
