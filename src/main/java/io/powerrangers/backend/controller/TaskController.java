package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.TaskRequestDto;
import io.powerrangers.backend.dto.TaskResponseDto;
import io.powerrangers.backend.service.TaskService;
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
    public ResponseEntity<Void> createTask(@RequestBody TaskRequestDto dto) {
        taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TaskResponseDto>> getMyTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByUser(userId));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<Void> updateTask(@PathVariable Long taskId, @RequestBody TaskRequestDto dto) {
        taskService.updateTask(taskId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeTask(@PathVariable Long taskId, @RequestBody TaskRequestDto dto) {
        taskService.removeTask(taskId, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Long taskId, @RequestBody TaskRequestDto dto) {
        taskService.changeStatus(taskId, dto.getUserId());
        return ResponseEntity.ok().build();
    }
}



