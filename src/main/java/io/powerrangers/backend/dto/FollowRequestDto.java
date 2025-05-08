package io.powerrangers.backend.dto;

import lombok.Getter;

@Getter
public class FollowRequestDto {

    private final Long followerId;

    private final Long followingId;

    public FollowRequestDto(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
