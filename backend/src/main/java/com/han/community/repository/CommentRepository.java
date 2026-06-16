package com.han.community.repository;

import com.han.community.dto.UserDto;
import com.han.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
SELECT c FROM Comment c
JOIN FETCH c.user
JOIN FETCH c.post
WHERE c.post.id = :postId
AND c.parentComment.id IS NULL
""")
    Page<Comment> findByPostIdAndParentCommentIsNull(@Param("postId") Long postId, Pageable pageable);

    @Query("""
SELECT c FROM Comment c
JOIN FETCH c.user
WHERE c.parentComment.id = :parentId
""")
    Page<Comment> findByParentCommentId(@Param("parentId")Long parentId, Pageable pageable);

    @Modifying
    @Query("UPDATE Comment c SET c.replyCount = c.replyCount + 1 WHERE c.id = :parentId")
    void increaseReplyCount(@Param("parentId")Long parentId);

    @Modifying
    void deleteByPostId(Long postId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.parentComment.id = :parentId")
    void deleteByParentCommentId(Long parentId);

    @Query("""
SELECT new com.han.community.dto.UserDto$MyCommentResponse(
    c.id,
    c.content,
    c.createdAt,
    p.id,
    p.title
)
FROM Comment c
JOIN c.post p
WHERE c.user.id = :userId
""")
    Page<UserDto.MyCommentResponse> findMyComments(@Param("userId") Long userId, Pageable pageable);
}
