package io.powerrangers.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force=true)
public class FollowRequestDto {

    @NotNull(message = "팔로잉 ID는 필수입니다.")
    private final Long followingId;

}
