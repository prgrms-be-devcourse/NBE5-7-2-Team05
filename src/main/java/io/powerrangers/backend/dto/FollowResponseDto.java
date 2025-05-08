package io.powerrangers.backend.dto;

public class FollowResponseDto {

    private Long followId;
    private Long followerId;
    private Long followingId;

    public FollowResponseDto(Long followId, Long followerId, Long followingId) {
        this.followId = followId;
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
