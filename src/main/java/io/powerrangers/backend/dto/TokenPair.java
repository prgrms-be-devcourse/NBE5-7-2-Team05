package io.powerrangers.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenPair {
    private String accessToken;
    private String refreshToken;
}
