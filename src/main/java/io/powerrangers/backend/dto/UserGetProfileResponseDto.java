package io.powerrangers.backend.dto;

import lombok.Builder;


public class UserGetProfileResponseDto extends UserProfileBaseDto {
    @Builder
    public UserGetProfileResponseDto(String nickname, String intro, String profileImage) {
        super(nickname, intro, profileImage);
    }
}
