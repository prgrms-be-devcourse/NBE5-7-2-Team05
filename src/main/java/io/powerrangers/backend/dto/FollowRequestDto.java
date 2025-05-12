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

    // TODO : 이후 Authentication에서 로그인한 사용자의 ID를 꺼내서 사용해야 하기 때문에 나중에 Dto에서 빼주기
    @NotNull(message = "팔로워 ID는 필수입니다.")
    private final Long followerId;

    @NotNull(message = "팔로잉 ID는 필수입니다.")
    private final Long followingId;

}
