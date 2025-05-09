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

    @NotNull(message="카테고리를 지정하지 않았습니다")
    private final String category;

    @NotNull(message="내용을 지정하지 않았습니다")
    private final String content;

    @NotNull(message = "공개 범위를 지정하지 않았습니다")
    private final TaskScope scope;

    @NotNull(message = "사용자 ID를 지정하지 않았습니다")
    private final Long userId;  // 시큐리티 적용 전 테스트용 사용자 ID

}
