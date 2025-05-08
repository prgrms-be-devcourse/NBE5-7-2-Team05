package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.FollowRepository;
import io.powerrangers.backend.dto.FollowRequestDto;
import io.powerrangers.backend.dto.FollowResponseDto;
import io.powerrangers.backend.entity.Follow;
import io.powerrangers.backend.entity.User;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    // TODO : follower는 이후 Authentication에서 ID 받아오기
    @Transactional
    public FollowResponseDto follow(FollowRequestDto request){
        User follower = userRepository.findById(request.getFollowerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        User following = userRepository.findById(request.getFollowingId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if( followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("이미 팔로우한 사용자입니다.");
        }

        Follow follow = Follow.builder()
                        .follower(follower)
                        .following(following)
                        .build();

        try {
            followRepository.save(follow);
        } catch (DataIntegrityViolationException e){
            throw new RuntimeException("이미 팔로우한 사용자입니다.");
        }

        return FollowResponseDto.builder()
                .followId(follow.getId())
                .followerId(follow.getFollower().getId())
                .followingId(follow.getFollowing().getId())
                .build();
    }

    // TODO : follower는 이후 Authentication에서 ID 받아오기
    @Transactional
    public void unfollow(Long followingId) {
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // TODO : 이후 하드 코딩 지우기
        User follower = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

//        User follower = Authentication의 id를 꺼내서.. ContextService에서 메서드 구현..
//        User follower = userRepository.findById(ContextService.getCurrentId())
//                          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new NoSuchElementException("팔로우 관계를 찾을 수 없습니다."));

        followRepository.delete(follow);
    }

}
