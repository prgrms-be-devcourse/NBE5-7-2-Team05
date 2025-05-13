package io.powerrangers.backend.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CommentCreateRequestDto {
    @NotNull(message = "할 일을 존재하지 않습니다.")
    private final Long taskId;

    private final Long parentId;

    @NotBlank(message = "내용을 입력해주세요")
    private final String content;
}