package io.powerrangers.backend.service;

import io.powerrangers.backend.dto.UserDetails;
import io.powerrangers.backend.exception.CustomException;
import io.powerrangers.backend.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ContextUtil {

    private ContextUtil() {
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return principal.getId();
    }
}
