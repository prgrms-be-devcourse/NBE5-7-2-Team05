package io.powerrangers.backend.service;

import io.powerrangers.backend.dto.TaskRequestDto;
import io.powerrangers.backend.dto.TaskResponseDto;
import io.powerrangers.backend.dto.TaskStatus;
import io.powerrangers.backend.entity.Task;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.repository.TaskRepository;
import io.powerrangers.backend.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createTask(TaskRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Task task = Task.builder()
                .category(dto.getCategory())
                .content(dto.getContent())
                .dueDate(dto.getDueDate())
                .status(dto.getStatus())
                .taskImage(dto.getTaskImage())
                .scope(dto.getScope())
                .user(user)
                .build();

        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByUser(Long userId) {
        return taskRepository.findAllByUserId(userId)
                .stream()
                .map(TaskResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateTask(Long id, TaskRequestDto dto) {
        Task task = getTaskIfOwner(id, dto.getUserId());
        task.updateFrom(dto);
    }

    @Transactional
    public void removeTask(Long id, TaskRequestDto dto) {
        Task task = getTaskIfOwner(id, dto.getUserId());
        taskRepository.delete(task);
    }

    private Task getTaskIfOwner(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("할 일을 찾을 수 없습니다."));
        if (!task.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("할 일의 소유자가 아닙니다.");
        }
        return task;
    }

    @Transactional
    public void changeStatus(Long taskId, Long userId) {
        Task task = getTaskIfOwner(taskId, userId);
        TaskStatus status = task.getStatus();
        if (status == TaskStatus.INCOMPLETE) {
            task.setStatus(TaskStatus.COMPLETE);
        } else {
            task.setStatus(TaskStatus.INCOMPLETE);
        }
    }
}



