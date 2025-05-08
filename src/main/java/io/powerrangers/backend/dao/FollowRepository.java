package io.powerrangers.backend.dao;

import io.powerrangers.backend.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
