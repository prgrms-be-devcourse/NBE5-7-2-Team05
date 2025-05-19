package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.TaskRepository;
import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.comment.CommentCreateRequestDto;
import io.powerrangers.backend.dto.comment.CommentResponseDto;
import io.powerrangers.backend.dto.comment.CommentUpdateRequestDto;
import io.powerrangers.backend.dto.comment.CommentUpdateResponseDto;
import io.powerrangers.backend.entity.Comment;
import io.powerrangers.backend.entity.Task;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.dao.CommentRepository;
import io.powerrangers.backend.exception.CustomException;
import io.powerrangers.backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
    public CommentResponseDto createComment(CommentCreateRequestDto request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        User user = userRepository.findById(ContextUtil.getCurrentUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        }

        Comment comment = new Comment(task, user, parent, request.getContent());
        commentRepository.save(comment);
        return CommentResponseDto.from(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long taskId){
        taskRepository.findById(taskId).orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        List<Comment> allComments = commentRepository.findByTaskId(taskId);

        // 부모 댓글만 필터링
        List<Comment> parentComments = allComments.stream()
                .filter(c -> c.getParent() == null)
                .collect(Collectors.toList());

        return parentComments.stream()
                .map(parent -> toDto(parent, allComments))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentUpdateResponseDto updateComment(Long commentId, CommentUpdateRequestDto request){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        validateOwner(comment);

        comment.setContent(request.getContent());

        return CommentUpdateResponseDto.from(comment);
    }

    private static void validateOwner(Comment comment) {
        Long userId = ContextUtil.getCurrentUserId();
        if(!Objects.equals(comment.getUser().getId(),userId)){
            throw new CustomException(ErrorCode.NOT_THE_OWNER);
        }
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                        .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        validateOwner(comment);
        commentRepository.deleteById(commentId);
    }
    
    //조회 메서드에서 트리형태로 반환하기 위한 private 메서드
    private CommentResponseDto toDto(Comment parent, List<Comment> allComments) {
        List<CommentResponseDto> childrenDtos = allComments.stream()
                .filter(c -> c.getParent() != null && Objects.equals(parent.getId(), c.getParent().getId()))
                .map(child -> toDto(child, allComments))
                .collect(Collectors.toList());

        return CommentResponseDto.builder()
                .id(parent.getId())
                .content(parent.getContent())
                .nickname(parent.getUser().getNickname())
                .profileImage(parent.getUser().getProfileImage())
                .children(childrenDtos)
                .createdAt(parent.getCreatedAt())
                .build();
    }

}