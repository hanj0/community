package com.han.community.service;

import com.han.community.dto.CommentDto;
import com.han.community.entity.Comment;
import com.han.community.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

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
    public CommentDto.Response create(CommentDto.CreateRequest dto) {

        Comment comment = Comment.builder()
                .content(dto.getContent())
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
}
