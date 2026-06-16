package com.han.community.repository;

import com.han.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
""")
    Page<Post> findAllWithChannelAndUser(Pageable pageable);

    @Query("""
SELECT p FROM Post p
JOIN FETCH p.user
WHERE p.channel.id = :channelId
""")
    Page<Post> findAllByChannelId(@Param("channelId")Long channelId, Pageable pageable);

    @Query("""
SELECT p FROM Post p
JOIN FETCH p.user u
WHERE p.title LIKE concat('%', :search, '%')
OR u.username LIKE concat('%', :search, '%')
""")
    Page<Post> findAllBySearch(@Param("search")String search, Pageable pageable);

    @Query("""
SELECT p FROM Post p
JOIN FETCH p.user u
WHERE (p.title LIKE concat('%', :search, '%')
OR u.username LIKE concat('%', :search, '%'))
AND p.channel.id = :channelId
""")
    Page<Post> findAllByChannelIdAndSearch(@Param("channelId")Long channelId, @Param("search")String search, Pageable pageable);

    @Query("""
SELECT p FROM Post p
JOIN FETCH p.channel
JOIN FETCH p.user
WHERE p.likeCount >= :threshold
AND p.createdAt >= :from
ORDER BY p.likeCount DESC
""")
    Page<Post> findHotPosts(@Param("threshold")int threshold, @Param("from")LocalDateTime from, Pageable pageable);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void increaseViewCount(@Param("id")Long id);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + 1 WHERE p.id = :id")
    void increaseCommentCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = (SELECT COUNT(c) FROM Comment c WHERE c.post.id = :id) WHERE p.id = :id")
    void syncCommentCount(@Param("id") Long id);

    Page<Post> findByUserId(Long userId, Pageable pageable);
}
