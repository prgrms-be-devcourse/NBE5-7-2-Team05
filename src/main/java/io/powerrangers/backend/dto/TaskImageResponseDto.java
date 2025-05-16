package io.powerrangers.backend.dto;

import io.powerrangers.backend.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TaskImageResponseDto {
    private final Long taskId;
    private final String imageUrl;

    public static TaskImageResponseDto from(Task task) {
        return TaskImageResponseDto.builder()
                .taskId(task.getId())
                .imageUrl(task.getTaskImage())
                .build();
    }
}
