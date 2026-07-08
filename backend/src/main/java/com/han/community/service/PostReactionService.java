package com.han.community.service;

import com.han.community.dto.PostReactionDto;
import com.han.community.entity.Post;
import com.han.community.entity.PostReaction;
import com.han.community.entity.ReactionType;
import com.han.community.entity.User;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.PostReactionRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostReactionService {

    private final PostReactionRepository postReactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void reactToPost(Long postId, Long userId, PostReactionDto.Request requestDto) {

        if(!postRepository.existsById(postId)) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        ReactionType newType = requestDto.type();
        ReactionType oldType = (newType == ReactionType.LIKE) ? ReactionType.DISLIKE : ReactionType.LIKE;

        int updated = postReactionRepository.updateTypeIfDifferent(postId, userId, newType, oldType);
        if(updated > 0) {
            decrementCount(postId, oldType);
            incrementCount(postId, newType);
            return;
        }

        int inserted = postReactionRepository.insertTypeIfNotExists(postId, userId, newType.name());
        if(inserted > 0) {
            incrementCount(postId, newType);
        }
    }

    @Transactional
    public void deleteReaction(Long postId, Long userId) {

        String deletedType = postReactionRepository.deleteAndReturnType(postId, userId);
        if(deletedType == null) return;
        decrementCount(postId, ReactionType.valueOf(deletedType));
    }

    private void incrementCount(Long postId, ReactionType type) {
        if(type == ReactionType.LIKE) {
            postRepository.incrementLikeCount(postId);
        }
        else if(type == ReactionType.DISLIKE) {
            postRepository.incrementDislikeCount(postId);
        }
    }

    private void decrementCount(Long postId, ReactionType type) {
        if(type == ReactionType.LIKE) {
            postRepository.decrementLikeCount(postId);
        }
        else if(type == ReactionType.DISLIKE) {
            postRepository.decrementDislikeCount(postId);
        }
    }
}
