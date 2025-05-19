package io.powerrangers.backend.dto.comment;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CommentUpdateRequestDto {
    @NotBlank(message="내용을 입력해주세요.")
    private final String content;
}
