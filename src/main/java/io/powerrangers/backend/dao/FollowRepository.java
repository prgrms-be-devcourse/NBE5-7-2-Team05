package io.powerrangers.backend.dao;

import io.powerrangers.backend.dto.UserFollowResponseDto;
import io.powerrangers.backend.entity.Follow;
import io.powerrangers.backend.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    @Query("select new io.powerrangers.backend.dto.UserFollowResponseDto(u.id, u.nickname, u.intro, u.profileImage) from Follow f JOIN f.follower u where f.following.id = :userId")
    List<UserFollowResponseDto> findFollowersByUser(Long userId);
}
