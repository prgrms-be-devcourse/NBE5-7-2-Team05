package io.powerrangers.backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskRequestDto {
    private String category;
    private String content;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private String taskImage;
    private TaskScope scope;

    private Long userId;  // Postman 테스트용 사용자 ID

    @Builder
    public TaskRequestDto(String category, LocalDateTime dueDate, String content, TaskStatus status, String taskImage, TaskScope scope, Long userId) {
        this.category = category;
        this.dueDate = dueDate;
        this.content = content;
        this.status = status;
        this.taskImage = taskImage;
        this.scope = scope;
        this.userId = userId;
    }
}

