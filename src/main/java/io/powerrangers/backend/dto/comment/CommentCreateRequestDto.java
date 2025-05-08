package io.powerrangers.backend.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateRequestDto {
    @NotNull(message = "유저가 존재하지 않습니다.")
    private Long taskId;

    @NotNull(message = "할 일이 존재하지 않습니다.")
    private Long userId;

    private Long parentId;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;
}