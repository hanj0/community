package com.han.community.service;

import com.han.community.entity.Bookmark;
import com.han.community.entity.Post;
import com.han.community.entity.User;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.BookmarkRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
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
