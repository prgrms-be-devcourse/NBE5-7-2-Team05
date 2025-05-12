package io.powerrangers.backend.controller;

import io.powerrangers.backend.dao.CommentRepository;
import io.powerrangers.backend.dto.comment.CommentCreateRequestDto;
import io.powerrangers.backend.dto.comment.CommentResponseDto;
import io.powerrangers.backend.dto.comment.CommentUpdateRequestDto;
import io.powerrangers.backend.dto.comment.CommentUpdateResponseDto;
import io.powerrangers.backend.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    /***
     *
     * @param request <- 현재 userid를 requestbody로 받는중. 추후 리팩토링
     * @return
     */
    @PostMapping
    public ResponseEntity<String> createComment(@Valid @RequestBody CommentCreateRequestDto request){
        commentService.createComment(request.getUserId(),request);
        return ResponseEntity.ok("Comment created");
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long taskId){
        List<CommentResponseDto> comments = commentService.getComments(taskId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentUpdateResponseDto> updateComment(@PathVariable Long commentId,
                                              @Valid @RequestBody CommentUpdateRequestDto request){
        CommentUpdateResponseDto response = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(response);
    }
}