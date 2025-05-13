package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.BaseResponse;
import io.powerrangers.backend.dto.SuccessCode;
import io.powerrangers.backend.dto.TaskCreateRequestDto;
import io.powerrangers.backend.dto.TaskResponseDto;
import io.powerrangers.backend.dto.TaskUpdateRequestDto;
import io.powerrangers.backend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<BaseResponse<?>> changeStatus(@PathVariable Long taskId) {
        taskService.changeStatus(taskId);
        return BaseResponse.success(SuccessCode.MODIFIED_SUCCESS);
    }
}



