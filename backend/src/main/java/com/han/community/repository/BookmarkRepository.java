package com.han.community.repository;

import com.han.community.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    void deleteByPostIdAndUserId(Long postId, Long userId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    @Modifying
    @Query(value = """
INSERT INTO Bookmark (post_id, user_id, created_at)
VALUES (:postId, :userId, now())
ON CONFLICT (post_id, user_id) DO NOTHING
""", nativeQuery = true)
    void upsertBookmark(@Param("postId")Long postId, @Param("userId")Long userId);
}
