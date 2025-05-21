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
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUserId(Long userId);
    @Query("select t from Task t join t.user u where u.id = :userId and t.scope != 'PRIVATE'")
    List<Task> findTasksForFollowers(Long userId);
    @Query("select t from Task t join t.user u where u.id = :userId and t.scope = 'PUBLIC'")
    List<Task> findTasksForPublic(Long userId);

    @Query("""
    SELECT FUNCTION('DATE', t.dueDate), COUNT(t)
    FROM Task t
    WHERE t.user.id = :targetUserId
      AND t.dueDate BETWEEN :start AND :end
      AND (
          (:scope = 'PUBLIC' AND t.scope = 'PUBLIC')
          OR (:scope = 'FOLLOWERS' AND (t.scope = 'PUBLIC' OR t.scope = 'FOLLOWERS'))
          OR (:scope = 'PRIVATE' AND t.user.id = :currentUserId)
      )
    GROUP BY FUNCTION('DATE', t.dueDate)
""")
    List<Object[]> countTasksByDateWithScope(
            @Param("targetUserId") Long targetUserId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("scope") String scope,
            @Param("currentUserId") Long currentUserId);

}

