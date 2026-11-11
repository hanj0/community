package com.han.community.user.service;

import com.han.community.comment.repository.CommentRepository;
import com.han.community.post.dto.PostDto;
import com.han.community.post.repository.PostRepository;
import com.han.community.user.dto.UserDto;
import com.han.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public UserDto.StatsResponse getStats(Long userId) {

        return userRepository.findUserStats(userId);
    }

    @Transactional
    public Page<PostDto.SummaryResponse> getMyPosts(Long userId, Pageable pageable) {

        pageable = PageRequest.of(
                Math.max(pageable.getPageNumber(), 0),
                Math.min(pageable.getPageSize(), 100),
                Sort.by("createdAt").descending()
        );

        return postRepository.findByUserId(userId, pageable)
                .map(post -> PostDto.SummaryResponse.from(post));
    }

    @Transactional
    public Page<UserDto.MyCommentResponse> getMyComments(Long userId, Pageable pageable) {

        pageable = PageRequest.of(
                Math.max(pageable.getPageNumber(), 0),
                Math.min(pageable.getPageSize(), 100),
                Sort.by("createdAt").descending()
        );

        return commentRepository.findMyComments(userId, pageable);
    }

    @Transactional
    public Page<PostDto.SummaryResponse> getMyBookmarks(Long userId, Pageable pageable) {

        pageable = PageRequest.of(
                Math.max(pageable.getPageNumber(), 0),
                Math.min(pageable.getPageSize(), 100),
                Sort.by("createdAt").descending()
        );

        return postRepository.findUserBookmarks(userId, pageable)
                .map(post -> PostDto.SummaryResponse.from(post));
    }
}
