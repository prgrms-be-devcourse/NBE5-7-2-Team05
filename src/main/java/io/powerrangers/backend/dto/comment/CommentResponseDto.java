package io.powerrangers.backend.dto.comment;

import io.powerrangers.backend.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final LocalDateTime createdAt;

    public static CommentResponseDto from(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getUser().getNickname())
                .profileImage(comment.getUser().getProfileImage()) // 없으면 null
                .createdAt(comment.getCreatedAt())
                .children(new ArrayList<>()) // 생성 시 자식 댓글은 비워둡니다
                .build();
    }
}