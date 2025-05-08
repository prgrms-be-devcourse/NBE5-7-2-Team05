package io.powerrangers.backend.dto;

import lombok.Builder;

public class FollowResponseDto {

    private Long followId;
    private Long followerId;
    private Long followingId;

    @Builder
    public FollowResponseDto(Long followId, Long followerId, Long followingId) {
        this.followId = followId;
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
