package io.powerrangers.backend.dto;

import io.powerrangers.backend.entity.Task;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskResponseDto {
    private Long id;
    private String category;
    private String content;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private String taskImage;
    private TaskScope scope;
    private String nickname;

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

