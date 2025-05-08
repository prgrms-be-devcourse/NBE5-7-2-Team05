package io.powerrangers.backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileBaseDto {

    private String nickname;
    private String intro;
    private String profileImage;

}
