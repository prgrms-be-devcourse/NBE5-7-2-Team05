package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.BaseResponse;
import io.powerrangers.backend.dto.SuccessCode;
import io.powerrangers.backend.dto.comment.CommentCreateRequestDto;
import io.powerrangers.backend.dto.comment.CommentResponseDto;
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
    public ResponseEntity<BaseResponse<?>> createComment(@Valid @RequestBody CommentCreateRequestDto request){
        commentService.createComment(request.getUserId(),request);
        return BaseResponse.success(SuccessCode.ADDED_SUCCESS);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<BaseResponse<List<CommentResponseDto>>> getComments(@PathVariable Long taskId){
        List<CommentResponseDto> comments = commentService.getComments(taskId);
        return BaseResponse.success(SuccessCode.GET_SUCCESS, comments);
    }
}