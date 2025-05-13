package io.powerrangers.backend.service;

import io.powerrangers.backend.dto.Role;
import io.powerrangers.backend.dto.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ContextUtil {

    private ContextUtil() {
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증된 사용자가 없습니다.");
        }

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return principal.getId();
    }
}
