package io.powerrangers.backend.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponseDto {
    private final Long id;
    private final String content;
    private final String nickname;
    private final String profileImage;
    private final List<CommentResponseDto> children;
}