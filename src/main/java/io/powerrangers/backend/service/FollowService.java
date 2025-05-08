package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.FollowRepository;
import io.powerrangers.backend.dto.FollowRequestDto;
import io.powerrangers.backend.dto.FollowResponseDto;
import io.powerrangers.backend.entity.Follow;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findById(request.getFollowingId())
                .orElseThrow(() -> new IllegalArgumentException("Following not found"));

        Follow follow = Follow.builder()
                        .follower(follower)
                        .following(following)
                        .build();

        followRepository.save(follow);

        return new FollowResponseDto(follow.getId(), follow.getFollower().getId(), follow.getFollower().getId());
    }

}
