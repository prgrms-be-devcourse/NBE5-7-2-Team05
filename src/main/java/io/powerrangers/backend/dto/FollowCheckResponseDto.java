package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FollowCheckResponseDto {
    private Long userId;
    private boolean following;
}
