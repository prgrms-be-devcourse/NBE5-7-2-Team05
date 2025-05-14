package io.powerrangers.backend.entity;

import io.powerrangers.backend.dto.TaskCreateRequestDto;
import io.powerrangers.backend.dto.TaskScope;
import io.powerrangers.backend.dto.TaskStatus;
import io.powerrangers.backend.dto.TaskUpdateRequestDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;

import java.util.List;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task extends BaseEntity {
    @Id
    @Column(name = "task_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String category;

    @Column(length = 50, nullable = false)
    private String content;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private TaskStatus status = TaskStatus.INCOMPLETE;

    @Column(name = "task_image")
    private String taskImage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskScope scope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "task", cascade= CascadeType.REMOVE)
    private List<Comment> comments;

    @Builder
    public Task(String category, String content, LocalDateTime dueDate, TaskStatus status, String taskImage,
                TaskScope scope, User user) {
        this.category = category;
        this.content = content;
        this.dueDate = dueDate;
        this.status = status;
        this.taskImage = taskImage;
        this.scope = scope;
        this.user = user;
    }

    public void updateFrom(TaskUpdateRequestDto dto) {
        this.category = dto.getCategory();
        this.content = dto.getContent();
        this.scope = dto.getScope();
    }

}
