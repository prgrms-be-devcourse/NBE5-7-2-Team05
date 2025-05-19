package io.powerrangers.backend.dto;

import io.powerrangers.backend.entity.User;
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

    public static UserFollowResponseDto from(User user) {
        return UserFollowResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .intro(user.getIntro())
                .profileImage(user.getProfileImage())
                .build();
    }
}
