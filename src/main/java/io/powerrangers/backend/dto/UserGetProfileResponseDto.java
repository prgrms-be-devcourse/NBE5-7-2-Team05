package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
public class UserGetProfileResponseDto {

    private final String nickname;
    private final String intro;
    private final String profileImage;

}
