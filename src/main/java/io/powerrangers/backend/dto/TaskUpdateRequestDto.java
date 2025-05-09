package io.powerrangers.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TaskUpdateRequestDto {

    @NotNull
    private final String category;

    @NotNull
    private final String content;

    @NotNull
    private final TaskScope scope;

    @NotNull
    private final Long userId;  // 시큐리티 적용 전 테스트용 사용자 ID

}
