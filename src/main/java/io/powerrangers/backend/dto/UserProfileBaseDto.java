package io.powerrangers.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileBaseDto {

    private String nickname;
    private String intro;
    private String profileImage;

}
