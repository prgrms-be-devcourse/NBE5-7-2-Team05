package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class FollowCheckResponseDto {
    private Long userId;
    private Boolean following;
}
