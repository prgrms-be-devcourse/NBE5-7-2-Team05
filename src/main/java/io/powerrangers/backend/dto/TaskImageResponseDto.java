package io.powerrangers.backend.dto;

import io.powerrangers.backend.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TaskImageResponseDto {
    private final Long taskId;
    private final String imageUrl;
    private final TaskStatus status;
    private final LocalDateTime dueDate;

    public static TaskImageResponseDto from(Task task) {
        return TaskImageResponseDto.builder()
                .taskId(task.getId())
                .imageUrl(task.getTaskImage())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .build();
    }
}
