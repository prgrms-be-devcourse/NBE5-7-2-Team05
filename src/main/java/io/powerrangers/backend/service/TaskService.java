package io.powerrangers.backend.service;

import static java.util.stream.Collectors.toList;

import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.*;
import io.powerrangers.backend.entity.Task;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.dao.TaskRepository;
import io.powerrangers.backend.exception.CustomException;
import io.powerrangers.backend.exception.ErrorCode;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final FollowService followService;

    @Transactional
    public void createTask(TaskCreateRequestDto dto) {
        Long userId = ContextUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
        List<Task> tasks = getTasksByScope(userId);

        return tasks.stream()
                .map(task -> TaskResponseDto.from(task))
                .collect(toList());
    }

    @Transactional
    public void updateTask(Long id, TaskUpdateRequestDto dto) {
        Task task = getTaskIfOwner(id);
        task.updateFrom(dto);
    }

    @Transactional
    public void removeTask(Long id) {
        Task task = getTaskIfOwner(id);
        taskRepository.delete(task);
    }

    private Task getTaskIfOwner(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
        Long userId = ContextUtil.getCurrentUserId();
        if (!task.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_THE_OWNER);
        }
        return task;
    }

    @Transactional
    public void changeStatus(Long taskId) {
        Task task = getTaskIfOwner(taskId);
        TaskStatus status = task.getStatus();
        if (status == TaskStatus.INCOMPLETE) {
            task.setStatus(TaskStatus.COMPLETE);
        } else {
            task.setStatus(TaskStatus.INCOMPLETE);
        }
    }

    @Transactional
    public String uploadTaskImage(MultipartFile file, Long taskId) throws IOException{
        validFile(file);
        String imageUrl = s3Service.upload(file);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
        task.setTaskImage(imageUrl);

        return imageUrl;
    }

    private void validFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new CustomException(ErrorCode.UNSUPPORTED_RESOURCE);
        }
    }

    public List<TaskImageResponseDto> getTaskImages(Long userId) {
        List<Task> tasks = getTasksByScope(userId);
        return tasks.stream()
                .map(task -> TaskImageResponseDto.from(task))
                .collect(toList());
    }

    private List<Task> getTasksByScope(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        TaskScope scope = followService.checkFollowingRelationship(userId);
        List<Task> tasks;
        if(scope.equals(TaskScope.PRIVATE)){
            tasks = taskRepository.findAllByUserId(userId);
        } else if (scope.equals(TaskScope.FOLLOWERS)){
            tasks = taskRepository.findTasksForFollowers(userId);
        } else {
            tasks = taskRepository.findTasksForPublic(userId);
        }
        return tasks;
    }

    public TaskResponseDto getTaskByImage(String imageUrl) {
        TaskResponseDto taskResponseDto = taskRepository.findTaskByImageUrl(imageUrl)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
        
        return taskResponseDto;
    }
}



