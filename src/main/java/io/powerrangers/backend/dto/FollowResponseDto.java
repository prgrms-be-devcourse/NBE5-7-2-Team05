package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class FollowResponseDto {

    private final Long followId;
    private final Long followerId;
    private final Long followingId;

    @Builder
    public FollowResponseDto(Long followId, Long followerId, Long followingId) {
        this.followId = followId;
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
