package com.han.community.repository;

import com.han.community.entity.PostReaction;
import com.han.community.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    Optional<PostReaction> findByPostIdAndUserId(Long postId, Long userId);

    @Modifying
    @Query("""
UPDATE PostReaction pr
SET pr.type = :newType
WHERE pr.post.id = :postId
AND pr.user.id = :userId
AND pr.type = :oldType
""")
    int updateTypeIfDifferent(@Param("postId")Long postId, @Param("userId")Long userId, @Param("newType")ReactionType newType, @Param("oldType")ReactionType oldType);

    @Modifying
    @Query(value = """
INSERT INTO post_reaction(post_id, user_id, type, created_at)
VALUES (:postId, :userId, :newType, now())
ON CONFLICT (post_id, user_id) DO NOTHING
""", nativeQuery = true)
    int insertTypeIfNotExists(@Param("postId")Long postId, @Param("userId")Long userId, @Param("newType")String newType);

    @Query(value = """
DELETE FROM post_reaction
WHERE post_id = :postId
AND user_id = :userId
RETURNING type
""", nativeQuery = true)
    String deleteAndReturnType(@Param("postId")Long postId, @Param("userId")Long userId);

}
