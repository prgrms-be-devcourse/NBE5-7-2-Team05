package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.FollowRepository;
import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.FollowRequestDto;
import io.powerrangers.backend.dto.FollowResponseDto;
import io.powerrangers.backend.dto.UserFollowResponseDto;
import io.powerrangers.backend.entity.Follow;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.exception.CustomException;
import io.powerrangers.backend.exception.ErrorCode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User following = userRepository.findById(request.getFollowingId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if( followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new CustomException(ErrorCode.ALREADY_FOLLOWED);
        }

        Follow follow = Follow.builder()
                        .follower(follower)
                        .following(following)
                        .build();

        try {
            followRepository.save(follow);
        } catch (DataIntegrityViolationException e){
            throw new CustomException(ErrorCode.ALREADY_FOLLOWED);
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // TODO : 이후 하드 코딩 지우기
        User follower = userRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

//        User follower = Authentication의 id를 꺼내서.. ContextService에서 메서드 구현..
//        User follower = userRepository.findById(ContextService.getCurrentId())
//                          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new CustomException(ErrorCode.FOLLOW_NOT_FOUND));

        followRepository.delete(follow);
    }

    @Transactional(readOnly=true)
    public List<UserFollowResponseDto> findFollowers(Long userId){
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 팔로잉에 userId가 있어야 한다.
        return followRepository.findFollowersByUser(userId);
    }

    @Transactional(readOnly=true)
    public List<UserFollowResponseDto> findFollowings(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 팔로워 id에 userId가 있어야 한다.
        return followRepository.findFollowingsByUser(userId);
    }
}
