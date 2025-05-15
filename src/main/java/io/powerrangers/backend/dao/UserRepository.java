package io.powerrangers.backend.dao;

import io.powerrangers.backend.dto.UserGetProfileResponseDto;
import io.powerrangers.backend.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("select new io.powerrangers.backend.dto.UserGetProfileResponseDto(u.nickname, u.intro, u.profileImage) from User u where u.nickname LIKE %:nickname%")
    List<UserGetProfileResponseDto> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
}
