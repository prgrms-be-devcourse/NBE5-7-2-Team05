package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserFollowResponseDto {
    private Long id;
    private String nickname;
    private String intro;
    private String profileImage;
}
