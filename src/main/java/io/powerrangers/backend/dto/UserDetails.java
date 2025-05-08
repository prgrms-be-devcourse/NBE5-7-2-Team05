package io.powerrangers.backend.dto;

import io.powerrangers.backend.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetails implements OAuth2User {
    private Long id;
    private String name;
    private String email;
    private String providerId;
    private String profileImage;
    private Map<String, Object> attributes;
    private Role role;

    public static UserDetails from(User user) {
        UserDetails details = new UserDetails();
        details.id = user.getId();
        details.name = user.getNickname();
        details.email = user.getEmail();
        details.role = user.getRole();
        details.providerId = user.getProviderId();
        details.profileImage = user.getProfileImage();
        return details;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Builder
    public UserDetails(String name, String email, String providerId, Map<String, Object> attributes, String profileImage) {
        this.name = name;
        this.email = email;
        this.providerId = providerId;
        this.attributes = attributes;
        this.profileImage = profileImage;
    }

    public void changeId(Long id) {
        this.id = id;
    }

    public void changeRole(Role role) {
        this.role = role;
    }
}
