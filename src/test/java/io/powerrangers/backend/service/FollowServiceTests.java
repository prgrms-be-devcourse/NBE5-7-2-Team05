package io.powerrangers.backend.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import io.powerrangers.backend.dao.FollowRepository;
import io.powerrangers.backend.dto.FollowRequestDto;
import io.powerrangers.backend.entity.Follow;
import io.powerrangers.backend.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FollowServiceTests {

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private FollowService followService;

    @Test
    @DisplayName("follow 정상 팔로우")
    void follow_success_test() throws Exception {

        // given
        User me = new User("user_1", "img/image_1.jpg", "google", "1234567890", "abc@gmail.com");
        User target = new User("user_2", "img/image_2.jpg", "google", "0987654321", "def@gmail.com");

        Mockito.when(followRepository.existsByFollowerAndFollowing(me, target))
                .thenReturn(false);

        FollowRequestDto requestDto = new FollowRequestDto(me.getId(), target.getId());

        // when
        followService.follow(requestDto);

        // then
        Mockito.verify(followRepository).save(any(Follow.class));
    }

    @Test
    @DisplayName("이미 팔로우한 경험이 있는 경우")
    void follow_fail_test() throws Exception {

        // given
        User me = new User("user_1", "img/image_1.jpg", "google", "1234567890", "abc@gmail.com");
        User target = new User("user_2", "img/image_2.jpg", "google", "0987654321", "def@gmail.com");

        Mockito.when(followRepository.existsByFollowerAndFollowing(me, target))
                .thenReturn(true);

        Assertions.assertThrows(IllegalStateException.class, () -> {
            followService.follow(new FollowRequestDto(me.getId(), target.getId()));
        });


    }
}