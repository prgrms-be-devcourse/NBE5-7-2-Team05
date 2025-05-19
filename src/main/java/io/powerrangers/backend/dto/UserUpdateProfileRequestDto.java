package io.powerrangers.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserUpdateProfileRequestDto{

    @NotBlank(message="닉네임을 지정해주세요.")
    private final String nickname;

    private final String intro;

    private final String profileImage;

}