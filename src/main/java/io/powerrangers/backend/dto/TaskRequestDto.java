package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TaskRequestDto {
    private final String category;
    private final String content;
    private final LocalDateTime dueDate;
    private final TaskStatus status;
    private final String taskImage;
    private final TaskScope scope;

    private final Long userId;  // Postman 테스트용 사용자 ID

}

