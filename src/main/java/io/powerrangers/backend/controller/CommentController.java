package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.comment.CommentCreateRequestDto;
import io.powerrangers.backend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

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
}