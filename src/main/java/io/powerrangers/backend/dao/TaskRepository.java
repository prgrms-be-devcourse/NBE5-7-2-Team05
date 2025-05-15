package io.powerrangers.backend.dao;

import io.powerrangers.backend.dto.TaskImageResponseDto;
import io.powerrangers.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUserId(Long userId);
    @Query("select new io.powerrangers.backend.dto.TaskImageResponseDto(t.id, t.taskImage) from Task t join t.user u where u.id = :userId")
    List<TaskImageResponseDto> findAllTaskImagesByUserId(Long userId);
}

