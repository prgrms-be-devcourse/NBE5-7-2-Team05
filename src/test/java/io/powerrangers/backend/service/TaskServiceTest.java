package io.powerrangers.backend.service;

import io.powerrangers.backend.dto.TaskRequestDto;
import io.powerrangers.backend.dto.TaskResponseDto;
import io.powerrangers.backend.dto.TaskScope;
import io.powerrangers.backend.dto.TaskStatus;
import io.powerrangers.backend.entity.Task;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.repository.TaskRepository;
import io.powerrangers.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private User user;
    private Task task;

    @BeforeEach
    void init() {
        user = User.builder()
                .nickname("test")
                .email("test@example.com")
                .provider("google")
                .providerId("1")
                .build();
        userRepository.save(user);

        task = Task.builder()
                .category("공부")
                .content("테스트 코드 작성")
                .dueDate(LocalDateTime.now().plusDays(1))
                .status(TaskStatus.INCOMPLETE)
                .taskImage(null)
                .scope(TaskScope.PUBLIC)
                .user(user)
                .build();
        taskRepository.save(task);

    }

    @Test
    @DisplayName("할 일 추가")
    void addTask() {
        TaskRequestDto dto = TaskRequestDto.builder()
                .category("공부")
                .content("알고리즘 공부")
                .dueDate(LocalDateTime.now().plusDays(1))
                .status(TaskStatus.INCOMPLETE)
                .taskImage(null)
                .scope(TaskScope.PUBLIC)
                .userId(user.getId())
                .build();

        taskService.createTask(dto);

        List<Task> tasks = taskRepository.findAllByUserId(user.getId());
        assertThat(tasks).anyMatch(t -> t.getContent().equals("알고리즘 공부"));
    }

    @Test
    @DisplayName("할 일 조회")
    void searchTask() {
        List<TaskResponseDto> userTasks = taskService.getTasksByUser(user.getId());

        assertThat(userTasks).isNotEmpty();
        assertThat(userTasks.get(0).getContent()).isEqualTo("테스트 코드 작성");
    }

    @Test
    @DisplayName("할 일 수정")
    void updateTask() {
        Long taskId = task.getId();
        Long userId = user.getId();

        TaskRequestDto updateDto = TaskRequestDto.builder()
                .userId(userId)
                .category("운동")
                .content("헬스장 가기")
                .dueDate(LocalDateTime.now().plusDays(1))
                .status(TaskStatus.COMPLETE)
                .taskImage(null)
                .scope(TaskScope.PRIVATE)
                .build();

        taskService.updateTask(taskId, updateDto);

        Task updatedTask = taskRepository.findById(taskId).orElseThrow();
        assertThat(updatedTask.getCategory()).isEqualTo("운동");
        assertThat(updatedTask.getContent()).isEqualTo("헬스장 가기");
        assertThat(updatedTask.getScope()).isEqualTo(TaskScope.PRIVATE);
    }

    @Test
    @DisplayName("할 일 삭제")
    void removeTask() {
        Long taskId = task.getId();
        Long userId = user.getId();

        TaskRequestDto deleteDto = TaskRequestDto.builder()
                .userId(userId)
                .build();

        taskService.removeTask(taskId, deleteDto);

        boolean exists = taskRepository.findById(taskId).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("할 일 완료")
    void completeTask() {
        Long taskId = task.getId();
        Long userId = user.getId();

        taskService.completeTask(taskId, userId);
        assertThat(taskRepository.findById(taskId).get().getStatus()).isEqualTo(TaskStatus.COMPLETE);

    }
}