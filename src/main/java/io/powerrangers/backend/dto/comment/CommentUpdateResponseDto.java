package io.powerrangers.backend.dto.comment;

import io.powerrangers.backend.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentUpdateResponseDto {
    private final Long id;
    private final String content;
    private final String nickname;

    public static CommentUpdateResponseDto from(Comment comment) {
        return CommentUpdateResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getUser().getNickname())
                .build();
    }
}
