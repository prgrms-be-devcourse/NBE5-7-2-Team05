package io.powerrangers.backend.service;

import io.powerrangers.backend.dto.comment.CommentCreateRequestDto;
import io.powerrangers.backend.dto.comment.CommentResponseDto;
import io.powerrangers.backend.entity.Comment;
import io.powerrangers.backend.entity.Task;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.dao.CommentRepository;
import io.powerrangers.backend.repository.TaskRepository;
import io.powerrangers.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/***
 TODO:
 Dtd에서의 입력검증 후, 유효성 검증을 위해 레포지토리를 주입받아야 하는 건 맞는 것 같은데,
 너무 많은 주입을 받고있진 않나..? 좋은 방법 모색해보기
 빌더패턴 사용 고려 중.
 task,user레포지토리는 임시생성하여 코드 짰습니다.
 ***/

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createComment(Long userId, CommentCreateRequestDto request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("해당 Task 없음"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음"));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 부모 댓글 없음"));
        }

        Comment comment = new Comment(task, user, parent, request.getContent());
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long taskId){
        List<Comment> allComments = commentRepository.findByTaskId(taskId);

        // 부모 댓글만 필터링
        List<Comment> parentComments = allComments.stream()
                .filter(c -> c.getParent() == null)
                .collect(Collectors.toList());

        return parentComments.stream()
                .map(parent -> toDto(parent, allComments))
                .collect(Collectors.toList());
    }

    private CommentResponseDto toDto(Comment parent, List<Comment> allComments) {
        List<CommentResponseDto> childrenDtos = allComments.stream()
                .filter(c -> parent.equals(c.getParent()))
                .map(child -> toDto(child, allComments))
                .collect(Collectors.toList());

        return CommentResponseDto.builder()
                .id(parent.getId())
                .content(parent.getContent())
                .nickname(parent.getUser().getNickname())
                .children(childrenDtos)
                .build();
    }

}