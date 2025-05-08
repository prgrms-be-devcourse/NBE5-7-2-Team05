package io.powerrangers.backend.dto;

import lombok.Builder;

public class UserUpdateProfileRequestDto extends UserProfileBaseDto {
    @Builder
    public UserUpdateProfileRequestDto(String nickname, String intro, String profileImage) {
        super(nickname, intro, profileImage);
    }
}
