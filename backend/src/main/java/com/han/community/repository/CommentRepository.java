package com.han.community.repository;

import com.han.community.dto.UserDto;
import com.han.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

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
    void incrementReplyCount(@Param("parentId")Long parentId);

    @Modifying
    void deleteByPostId(Long postId);

    @Modifying
    @Query("UPDATE Comment c SET c.deletedAt = :deletedAt WHERE c.parentComment.id = :parentId")
    void deleteByParentCommentId(@Param("parentId")Long parentId, @Param("deletedAt")LocalDateTime deletedAt);

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

    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :commentId")
    void incrementLikeCount(@Param("commentId") Long commentId);

    @Modifying
    @Query("UPDATE Comment c SET c.dislikeCount = c.dislikeCount + 1 WHERE c.id = :commentId")
    void incrementDislikeCount(@Param("commentId") Long commentId);

    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount - 1 WHERE c.id = :commentId")
    void decrementLikeCount(@Param("commentId") Long commentId);

    @Modifying
    @Query("UPDATE Comment c SET c.dislikeCount = c.dislikeCount - 1 WHERE c.id = :commentId")
    void decrementDislikeCount(@Param("commentId") Long commentId);

    @Query("SELECT c.user.id FROM Comment c WHERE c.id = :id")
    Long findUserIdById(@Param("id")Long id);

    /**
     * 게시글의 모든 댓글 & 대댓글을 삭제처리한다.
     *
     * @Param postId    대상 게시글 ID
     * @Param deletedAt 삭제시각 (게시글 삭제시각과 맞추기 위해 넘겨받음)
     */
    @Modifying
    @Query("UPDATE Comment c SET c.deletedAt = :deletedAt WHERE c.post.id = :postId")
    void deleteAllByPost(@Param("postId")Long postId, @Param("deletedAt")LocalDateTime deletedAt);
}
