package com.han.community.bookmark.service;

import com.han.community.post.entity.Post;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.bookmark.repository.BookmarkRepository;
import com.han.community.post.repository.PostRepository;
import com.han.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void setBookmark(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        bookmarkRepository.upsertBookmark(postId, userId);
    }

    @Transactional
    public void deleteBookmark(Long postId, Long userId) {

        bookmarkRepository.deleteByPostIdAndUserId(postId, userId);
    }

}
