package io.powerrangers.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileBaseDto {

    private final String nickname;
    private final String intro;
    private final String profileImage;

}
