package io.powerrangers.backend.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.powerrangers.backend.dao.FollowRepository;
import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.FollowRequestDto;
import io.powerrangers.backend.entity.Follow;
import io.powerrangers.backend.entity.User;
import java.lang.reflect.Field;
import java.util.Optional;
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
    private UserRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private FollowService followService;

    @Test
    @DisplayName("follow 정상 팔로우")
    void follow_success_test() throws Exception {

        // given
        User me = User.builder()
                .nickname("user_1")
                .build();
        User target = User.builder()
                .nickname("user_2")
                .build();

        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(me, 1L);
            idField.set(target, 2L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(userRepository.findById(1L)).thenReturn(Optional.of(me));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));

        when(followRepository.existsByFollowerAndFollowing(me, target))
                .thenReturn(false);

        FollowRequestDto requestDto = new FollowRequestDto(me.getId(), target.getId());

        // when
        followService.follow(requestDto);

        // then
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    @DisplayName("이미 팔로우한 경험이 있는 경우")
    void follow_fail_test() throws Exception {

        // given
        User me = User.builder()
                .nickname("user_1")
                .build();
        User target = User.builder()
                .nickname("user_2")
                .build();

        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(me, 1L);
            idField.set(target, 2L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(userRepository.findById(1L)).thenReturn(Optional.of(me));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));

        when(followRepository.existsByFollowerAndFollowing(me, target))
                .thenReturn(true);

        assertThatThrownBy(
                () -> {
                    followService.follow(new FollowRequestDto(me.getId(), target.getId()));
                }
        ).isInstanceOf(RuntimeException.class);

    }

    @Test
    @DisplayName("언팔로우 테스트")
    void unfollow_success_test() throws Exception {

        // given
        User me = User.builder()
                .nickname("user_1")
                .build();
        User target = User.builder()
                .nickname("user_2")
                .build();

        try{
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(me, 1L);
            idField.set(target, 2L);
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        // 특정 조건 시 가짜 리턴 값 설정
        when(userRepository.findById(1L)).thenReturn(Optional.of(me));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));

        Follow follow = Follow.builder()
                        .follower(me)
                        .following(target)
                        .build();

        when(followRepository.existsByFollowerAndFollowing(me, target))
                .thenReturn(true);
        doNothing().when(followRepository).delete(follow);

        // when
        followService.unfollow(2L);

        // verify() : 특정 메서드가 실제로 호출됐는지 검증
        verify(followRepository, times(1)).delete(follow);
    }
}