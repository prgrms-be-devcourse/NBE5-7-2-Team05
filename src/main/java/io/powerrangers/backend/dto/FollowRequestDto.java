package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowRequestDto {

    // TODO : 이후 Authentication에서 로그인한 사용자의 ID를 꺼내서 사용해야 하기 때문에 나중에 Dto에서 빼주기
    private Long followerId;

    private Long followingId;

    public FollowRequestDto(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
