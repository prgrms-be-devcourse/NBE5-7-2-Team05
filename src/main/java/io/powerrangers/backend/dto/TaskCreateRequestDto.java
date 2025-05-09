package io.powerrangers.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TaskCreateRequestDto {

    @NotNull
    private final String category;

    @NotNull
    private final String content;

    @NotNull
    @Future
    private final LocalDateTime dueDate;

    @NotNull
    private final TaskStatus status;

    private final String taskImage;

    @NotNull
    private final TaskScope scope;

    @NotNull
    private final Long userId;  // 시큐리티 적용 전 테스트용 사용자 ID

}

