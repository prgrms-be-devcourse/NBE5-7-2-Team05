package io.powerrangers.backend.repository;

import io.powerrangers.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Optional<Comment> findById(Long id);
}