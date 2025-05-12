package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.BaseResponse;
import io.powerrangers.backend.dto.SuccessCode;
import io.powerrangers.backend.dto.TaskCreateRequestDto;
import io.powerrangers.backend.dto.TaskResponseDto;
import io.powerrangers.backend.dto.TaskUpdateRequestDto;
import io.powerrangers.backend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<BaseResponse<?>> createTask(@RequestBody @Valid TaskCreateRequestDto dto) {
        taskService.createTask(dto);
        return BaseResponse.ok(SuccessCode.ADDED_SUCCESS);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<List<TaskResponseDto>>> getMyTasks(@PathVariable Long userId) {
        return BaseResponse.ok(SuccessCode.GET_SUCCESS, taskService.getTasksByUser(userId));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<BaseResponse<?>> updateTask(@PathVariable Long taskId, @RequestBody @Valid TaskUpdateRequestDto dto) {
        taskService.updateTask(taskId, dto);
        return BaseResponse.ok(SuccessCode.MODIFIED_SUCCESS);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<BaseResponse<?>> removeTask(@PathVariable Long taskId, @RequestBody @Valid TaskCreateRequestDto dto) {
        taskService.removeTask(taskId, dto);
        return BaseResponse.ok(SuccessCode.DELETED_SUCCESS);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<BaseResponse<?>> changeStatus(@PathVariable Long taskId, @RequestBody @Valid TaskCreateRequestDto dto) {
        taskService.changeStatus(taskId, dto.getUserId());
        return BaseResponse.ok(SuccessCode.MODIFIED_SUCCESS);
    }
}



