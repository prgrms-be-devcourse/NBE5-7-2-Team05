package io.powerrangers.backend.dao;

import io.powerrangers.backend.dto.TaskImageResponseDto;
import io.powerrangers.backend.dto.TaskResponseDto;
import io.powerrangers.backend.dto.TaskScope;
import io.powerrangers.backend.dto.TaskStatus;
import io.powerrangers.backend.entity.Task;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUserId(Long userId);
    @Query("select t from Task t join t.user u where u.id = :userId and t.scope != 'PRIVATE'")
    List<Task> findTasksForFollowers(Long userId);
    @Query("select t from Task t join t.user u where u.id = :userId and t.scope = 'PUBLIC'")
    List<Task> findTasksForPublic(Long userId);
}

