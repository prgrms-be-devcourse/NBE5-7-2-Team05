package io.powerrangers.backend.dto;

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
}

