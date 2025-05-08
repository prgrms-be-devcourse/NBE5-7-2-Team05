package io.powerrangers.backend.controller;

import com.sun.security.auth.UserPrincipal;
import io.powerrangers.backend.dto.TaskRequestDto;
import io.powerrangers.backend.dto.TaskResponseDto;
import io.powerrangers.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Void> createTask(@RequestBody TaskRequestDto dto, @AuthenticationPrincipal UserPrincipal user) {
        taskService.createTask(dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TaskResponseDto>> getMyTasks(@PathVariable Long userId, @AuthenticationPrincipal UserPrincipal user) {
        if (!user.getId().equals(userId)) { // 로그인한 사용자와 userId의 정보가 다름
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(taskService.getTasksByUser(userId));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<Void> updateTask(@PathVariable("taskId") Long taskId, @RequestBody TaskRequestDto dto, @AuthenticationPrincipal UserPrincipal user) {
        taskService.updateTask(taskId, dto, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeTask(@PathVariable Long taskId, @AuthenticationPrincipal UserPrincipal user) {
        taskService.removeTask(taskId, user.getId());
        return ResponseEntity.noContent().build();
    }

}



