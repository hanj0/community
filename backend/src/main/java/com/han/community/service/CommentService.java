package com.han.community.service;

import com.han.community.dto.CommentDto;
import com.han.community.entity.Comment;
import com.han.community.entity.Post;
import com.han.community.entity.User;
import com.han.community.repository.CommentRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public Page<CommentDto.Response> getComments(Long postId, Pageable pageable) {

        return commentRepository.findAll(pageable).map(
                c -> CommentDto.Response.builder()
                        .id(c.getId())
                        .content(c.getContent())
                        .build()
        );
    }

    @Transactional
    public CommentDto.Response create(CommentDto.CreateRequest requestDto, Long postId, Long userId) {

        User user = userRepository.getReferenceById(userId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        Comment parentComment = getParentComment(postId, requestDto.getParentCommentId());

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .parentComment(parentComment)
                .content(requestDto.getContent())
                .build();

        Comment save = commentRepository.save(comment);

        return CommentDto.Response.builder()
                .id(save.getId())
                .content(save.getContent())
                .build();
    }

    @Transactional
    public CommentDto.Response update(CommentDto.UpdateRequest dto) {

        Comment comment = commentRepository.findById(dto.getId()).orElseThrow();
        comment.update(dto.getContent());

        return CommentDto.Response.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .build();
    }

    @Transactional
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    private Comment getParentComment(Long postId, Long parentCommentId) {

        if(parentCommentId == null) return null;

        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글 없음"));

        // 같은 게시글 검증

        return parent;
    }
}
