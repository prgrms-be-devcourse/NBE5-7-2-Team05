package io.powerrangers.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TaskUpdateRequestDto {

    @NotBlank(message="카테고리를 지정하지 않았습니다")
    private final String category;

    @NotBlank(message="내용을 지정하지 않았습니다")
    private final String content;

    @NotNull(message = "공개 범위를 지정하지 않았습니다")
    private final TaskScope scope;

}
