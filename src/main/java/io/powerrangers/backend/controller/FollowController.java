package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.FollowRequestDto;
import io.powerrangers.backend.dto.FollowResponseDto;
import io.powerrangers.backend.entity.Follow;
import io.powerrangers.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // TODO : 이후 Authentication에서 ID 받아오기
    @PostMapping
    public FollowResponseDto follow(FollowRequestDto followRequestDto){
        return followService.follow(followRequestDto);
    }

    @DeleteMapping("/{followingId}")
    public void unfollow(@PathVariable Long followingId){
        followService.unfollow(followingId);
    }
}
