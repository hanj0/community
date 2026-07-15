package com.han.community.repository;

import com.han.community.dto.UserDto;
import com.han.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);

    @Query("""
SELECT new com.han.community.dto.UserDto$StatsResponse(
    (SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId),
    (SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId),
    (SELECT u.createdAt FROM User u WHERE u.id = :userId)
)
""")
    UserDto.StatsResponse findUserStats(@Param("userId") Long userId);

    List<User> findAllById(Iterable<Long> ids);
}
