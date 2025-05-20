package io.powerrangers.backend.service;

import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.*;
import io.powerrangers.backend.entity.Task;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.dao.TaskRepository;
import io.powerrangers.backend.exception.CustomException;
import io.powerrangers.backend.exception.ErrorCode;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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
                .toList();
    }

    protected List<Task> getTasksByScope(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        TaskScope scope = followService.checkScopeWithUser(userId);
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

    public TaskResponseDto getTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
        TaskScope scope = followService.checkScopeWithUser(task.getUser().getId());

        // scope가 private = 내 task 라는 얘기 -> task.scope에 상관없이 다 볼 수 있다.
        // scope가 followers = 맞팔 관계라는 얘기 -> task.scope의 Private 빼고 다 볼 수 있다. (followers, public)
        // scope가 public = 맞팔 x -> task.scope가 public 인 것을 볼 수 있다. 아니라면 예외
        // 아니라면 예외 -> 접근 권한이 없는 걸 보려고 함.

        if(scope.equals(TaskScope.PRIVATE)){ // Task가 나(로그인한 사람)의 게시물
            return TaskResponseDto.from(task);
        } else if(scope.equals(TaskScope.FOLLOWERS) && !task.getScope().equals(TaskScope.PRIVATE)){
            return TaskResponseDto.from(task);
        } else if(scope.equals(TaskScope.PUBLIC) && task.getScope().equals(TaskScope.PUBLIC)){
            return TaskResponseDto.from(task);
        }
        throw new CustomException(ErrorCode.NOT_ALLOWED);
    }

    @Transactional
    public void postpone(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
        task.setDueDate(task.getDueDate().plusHours(24));
        taskRepository.save(task);
    }

    public TaskSummaryResponseDto getMonthlyTaskSummary(Long targetUserId, int year, int month) {
        Long currentUserId = ContextUtil.getCurrentUserId();

        TaskScope scope = followService.checkScopeWithUser(targetUserId);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Object[]> result = taskRepository.countTasksByDateWithScope(
                targetUserId, start.atStartOfDay(), end.atTime(23, 59, 59),
                scope.name(), currentUserId);

        Map<LocalDate, Long> countMap = new HashMap<>();
        for (Object[] row : result) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Long count = (Long) row[1];
            countMap.put(date, count);
        }

        List<TaskSummaryResponseDto.DailySummary> dailySummaries = new ArrayList<>();
        for (int day = 1; day <= start.lengthOfMonth(); day++) {
            LocalDate currentDate = start.withDayOfMonth(day);
            int count = countMap.getOrDefault(currentDate, 0L).intValue();
            dailySummaries.add(new TaskSummaryResponseDto.DailySummary(currentDate.toString(), count));
        }

        return new TaskSummaryResponseDto(year, month, dailySummaries);
    }

}



