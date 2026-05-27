package com.han.community.repository;

import com.han.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
            SELECT p FROM Post p
            JOIN FETCH p.channel
            JOIN FETCH p.user
            WHERE p.id = :id
          """)
    Optional<Post> findByIdWithDetails(@Param("id") Long id);

    @Query("""
SELECT p FROM Post p
JOIN FETCH p.channel
JOIN FETCH p.user
WHERE p.likeCount >= :threshold
AND p.createdAt >= :from
ORDER BY p.likeCount DESC
""")
    Page<Post> findHotPosts(@Param("threshold")int threshold, @Param("from")LocalDateTime from, Pageable pageable);
}
