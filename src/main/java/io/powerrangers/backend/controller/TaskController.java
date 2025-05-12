package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.BaseResponse;
import io.powerrangers.backend.dto.SuccessCode;
import io.powerrangers.backend.dto.TaskCreateRequestDto;
import io.powerrangers.backend.dto.TaskResponseDto;
import io.powerrangers.backend.dto.TaskUpdateRequestDto;
import io.powerrangers.backend.service.S3Service;
import io.powerrangers.backend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<BaseResponse<?>> createTask(@Valid @RequestBody TaskCreateRequestDto dto) {
        taskService.createTask(dto);
        return BaseResponse.success(SuccessCode.ADDED_SUCCESS);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<List<TaskResponseDto>>> getMyTasks(@PathVariable Long userId) {
        return BaseResponse.success(SuccessCode.GET_SUCCESS, taskService.getTasksByUser(userId));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<BaseResponse<?>> updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskUpdateRequestDto dto) {
        taskService.updateTask(taskId, dto);
        return BaseResponse.success(SuccessCode.MODIFIED_SUCCESS);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<BaseResponse<?>> removeTask(@PathVariable Long taskId, @Valid @RequestBody TaskCreateRequestDto dto) {
        taskService.removeTask(taskId, dto);
        return BaseResponse.success(SuccessCode.DELETED_SUCCESS);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<BaseResponse<?>> changeStatus(@PathVariable Long taskId, @Valid @RequestBody TaskCreateRequestDto dto) {
        taskService.changeStatus(taskId, dto.getUserId());
        return BaseResponse.success(SuccessCode.MODIFIED_SUCCESS);
    }

    @PatchMapping("/{taskId}/image")
    public ResponseEntity<?> uploadImage(@RequestPart("image") MultipartFile file, @PathVariable Long taskId, @RequestPart TaskCreateRequestDto dto) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }
        if (!file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("이미지 파일만 업로드할 수 있습니다.");
        }

        try {
            String imageUrl = s3Service.upload(file);
            taskService.updateTaskImage(taskId, imageUrl);
            return ResponseEntity.ok().body(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패: " + e.getMessage());
        }
    }
}



