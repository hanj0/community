package com.han.community.post.viewcount;

import com.han.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "view-count.mode", havingValue = "db", matchIfMissing = true)
@RequiredArgsConstructor
public class DbViewCountIncrementer implements ViewCountIncrementer {

    private final PostRepository postRepository;

    @Override
    @Transactional
    public void increment(Long postId) {
        postRepository.incrementViewCount(postId);
    }
}
