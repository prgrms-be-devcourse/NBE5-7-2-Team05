package io.powerrangers.backend.dao;

import io.powerrangers.backend.entity.Follow;
import io.powerrangers.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);
}
