package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.BaseResponse;
import io.powerrangers.backend.dto.SuccessCode;
import io.powerrangers.backend.dto.TaskCreateRequestDto;
import io.powerrangers.backend.dto.TaskImageResponseDto;
import io.powerrangers.backend.dto.TaskResponseDto;
import io.powerrangers.backend.dto.TaskUpdateRequestDto;
import io.powerrangers.backend.service.S3Service;
import io.powerrangers.backend.service.TaskService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<BaseResponse<?>> createTask(@Valid @RequestBody TaskCreateRequestDto dto) {
        taskService.createTask(dto);
        return BaseResponse.success(SuccessCode.ADDED_SUCCESS);
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<BaseResponse<?>> updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskUpdateRequestDto dto) {
        taskService.updateTask(taskId, dto);
        return BaseResponse.success(SuccessCode.MODIFIED_SUCCESS);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<BaseResponse<?>> removeTask(@PathVariable Long taskId) {
        taskService.removeTask(taskId);
        return BaseResponse.success(SuccessCode.DELETED_SUCCESS);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<BaseResponse<?>> changeStatus(@PathVariable Long taskId) {
        taskService.changeStatus(taskId);
        return BaseResponse.success(SuccessCode.MODIFIED_SUCCESS);
    }

    @PatchMapping("/{taskId}/image")
    public ResponseEntity<BaseResponse<String>> uploadImage(@RequestPart("image") MultipartFile file, @PathVariable Long taskId) throws IOException {
        String imageUrl = taskService.uploadTaskImage(file, taskId);
        return BaseResponse.success(SuccessCode.MODIFIED_SUCCESS, imageUrl);
    }

    @GetMapping("/{userId}/images")
    public ResponseEntity<BaseResponse<List<TaskImageResponseDto>>> getTaskImages(@PathVariable Long userId) {
        return BaseResponse.success(SuccessCode.GET_SUCCESS, taskService.getTaskImages(userId));
    }

    @GetMapping("{taskId}")
    public ResponseEntity<BaseResponse<TaskResponseDto>> getTask(@PathVariable Long taskId) {
        return BaseResponse.success(SuccessCode.GET_SUCCESS, taskService.getTask(taskId));
    }
}



