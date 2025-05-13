package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.BaseResponse;
import io.powerrangers.backend.dto.SuccessCode;
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

    @PostMapping
    public ResponseEntity<BaseResponse<?>> createComment(@Valid @RequestBody CommentCreateRequestDto request){
        commentService.createComment(request);
        return BaseResponse.success(SuccessCode.ADDED_SUCCESS);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<BaseResponse<List<CommentResponseDto>>> getComments(@PathVariable Long taskId){
        List<CommentResponseDto> comments = commentService.getComments(taskId);
        return BaseResponse.success(SuccessCode.GET_SUCCESS, comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<BaseResponse<CommentUpdateResponseDto>> updateComment(@PathVariable Long commentId,
                                              @Valid @RequestBody CommentUpdateRequestDto request){
        CommentUpdateResponseDto response = commentService.updateComment(commentId, request);
        return BaseResponse.success(SuccessCode.MODIFIED_SUCCESS, response);
    }
}