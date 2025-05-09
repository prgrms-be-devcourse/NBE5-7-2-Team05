package io.powerrangers.backend.dto;

import io.powerrangers.backend.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TaskResponseDto {
    private final Long id;
    private final String category;
    private final String content;
    private final LocalDateTime dueDate;
    private final TaskStatus status;
    private final String taskImage;
    private final TaskScope scope;
    private final String nickname;

    public static TaskResponseDto from(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .category(task.getCategory())
                .content(task.getContent())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .taskImage(task.getTaskImage())
                .scope(task.getScope())
                .nickname(task.getUser().getNickname())
                .build();
    }
}

