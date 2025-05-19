package io.powerrangers.backend.dao;

import io.powerrangers.backend.dto.UserFollowResponseDto;
import io.powerrangers.backend.entity.Follow;
import io.powerrangers.backend.entity.User;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    @Query("select u from Follow f JOIN f.follower u where f.following.id = :userId")
    List<User> findFollowersByUser(Long userId);

    @Query("select u from Follow f JOIN f.following u where f.follower.id = :userId")
    List<User> findFollowingsByUser(Long userId);

    @Query("select count(f) from Follow f where f.following.id = :userId")
    Long countFollowersByUser(Long userId);
    @Query("select count(f) from Follow f where f.follower.id = :userId")
    Long countFollowingsByUser(Long userId);

    @Query("SELECT f FROM Follow f " +
            "JOIN FETCH f.follower u " +
            "WHERE f.following.id = :userId AND (:cursor IS NULL OR f.id < :cursor) " +
            "ORDER BY f.id ASC")
    List<Follow> findFollowersWithCursor(@Param("userId") Long userId,
                                         @Param("cursor") Long cursor,
                                         Pageable pageable);

    @Query("SELECT f FROM Follow f " +
            "JOIN FETCH f.following u " +
            "WHERE f.follower.id = :userId AND (:cursor IS NULL OR f.id < :cursor) " +
            "ORDER BY f.id ASC")
    List<Follow> findFollowingsWithCursor(@Param("userId") Long userId,
                                          @Param("cursor") Long cursor,
                                          Pageable pageable);
}
