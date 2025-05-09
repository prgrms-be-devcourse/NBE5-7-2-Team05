package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FollowResponseDto {

    private final Long followId;
    private final Long followerId;
    private final Long followingId;

}
