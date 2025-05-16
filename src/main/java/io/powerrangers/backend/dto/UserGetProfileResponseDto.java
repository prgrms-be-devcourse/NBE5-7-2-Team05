package io.powerrangers.backend.dto;

import io.powerrangers.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class UserGetProfileResponseDto {

    private final Long userId;
    private final String nickname;
    private final String intro;
    private final String profileImage;

    public static UserGetProfileResponseDto from(User user) {
        return UserGetProfileResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .intro(user.getIntro())
                .profileImage(user.getProfileImage())
                .build();
    }

}
