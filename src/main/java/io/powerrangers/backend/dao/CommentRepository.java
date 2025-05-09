package io.powerrangers.backend.dao;

import io.powerrangers.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Optional<Comment> findById(Long id);

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.task.id = :taskId")
    List<Comment> findByTaskId(@Param("taskId")Long taskId);
}