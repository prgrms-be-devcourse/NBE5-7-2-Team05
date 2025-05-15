package io.powerrangers.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message="카테고리를 지정하지 않았습니다.")
    private final String category;

    @NotBlank(message="내용을 지정하지 않았습니다.")
    private final String content;

    @NotNull(message = "기한을 지정하지 않았습니다.")
    @Future(message = "기한은 미래로 지정해야 합니다.")
    private final LocalDateTime dueDate;

    @NotNull(message = "상태를 지정하지 않았습니다.")
    private final TaskStatus status;

    private final String taskImage;

    @NotNull(message = "공개 범위를 지정하지 않았습니다.")
    private final TaskScope scope;

}

