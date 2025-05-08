package io.powerrangers.backend.service;

import io.powerrangers.backend.dto.TaskRequestDto;
import io.powerrangers.backend.dto.TaskResponseDto;
import io.powerrangers.backend.entity.Task;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.repository.TaskRepository;
import io.powerrangers.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public void createTask(TaskRequestDto dto, Long userId) {
        User user = userRepository.findById(userId)
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

    public List<TaskResponseDto> getTasksByUser(Long userId) {
        return taskRepository.findAllByUserId(userId)
                .stream()
                .map(TaskResponseDto::from)
                .collect(Collectors.toList());
    }

    public void updateTask(Long id, TaskRequestDto dto, Long userId) {
        Task task = getTaskIfOwner(id, userId);
        task.updateFrom(dto);
    }

    public void removeTask(Long id, Long userId) {
        Task task = getTaskIfOwner(id, userId);
        taskRepository.delete(task);
    }

    private Task getTaskIfOwner(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("할 일을 찾을 수 없습니다."));
        if (!task.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("할 일의 소유자가 아닙니다.");
        }
        return task;
    }

}


