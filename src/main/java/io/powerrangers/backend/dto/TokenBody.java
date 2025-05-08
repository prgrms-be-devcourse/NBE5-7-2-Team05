package io.powerrangers.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenBody {
    private Long userId;
    private String role;
}
