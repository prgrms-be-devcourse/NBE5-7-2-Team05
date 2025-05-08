package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.FollowRepository;
import io.powerrangers.backend.dto.FollowRequestDto;
import io.powerrangers.backend.dto.FollowResponseDto;
import io.powerrangers.backend.entity.Follow;
import io.powerrangers.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public FollowResponseDto follow(FollowRequestDto request){
        User follower = userRepository.findById(request.getFollowerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        User following = userRepository.findById(request.getFollowingId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Follow follow = Follow.builder()
                        .follower(follower)
                        .following(following)
                        .build();

        if( followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("이미 팔로우한 사용자입니다.");
        }

        try {
            followRepository.save(follow);
        } catch (DataIntegrityViolationException e){
            throw new RuntimeException("이미 팔로우한 사용자입니다.");
        }

        return new FollowResponseDto(follow.getId(), follow.getFollower().getId(), follow.getFollower().getId());
    }

}
