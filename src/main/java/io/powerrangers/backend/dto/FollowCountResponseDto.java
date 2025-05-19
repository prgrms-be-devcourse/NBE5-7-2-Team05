package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FollowCountResponseDto {
    private final Long userId;
    private final Long followerCount;
    private final Long followingCount;
}
