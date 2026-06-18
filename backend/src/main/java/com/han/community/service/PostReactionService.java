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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostReactionService {

    private final PostReactionRepository postReactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostReactionDto.Response reactToPost(Long postId, Long userId, PostReactionDto.Request requestDto) {

        User user = userRepository.getReferenceById(userId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        PostReaction reaction = postReactionRepository.findByPostIdAndUserId(postId, userId)
                .orElse(null);

        ReactionType oldType = reaction == null ? null : reaction.getType();
        ReactionType newType = requestDto.type();
        if(oldType == newType) {
            return new PostReactionDto.Response(
                    postId,
                    newType,
                    post.getLikeCount(),
                    post.getDislikeCount()
            );
        }

        if(reaction == null) {
            postReactionRepository.save(new PostReaction(post, user, newType));
        } else {
            reaction.changeType(newType);
        }

        int likeCount = post.getLikeCount();
        int dislikeCount = post.getDislikeCount();
/// reaction table을 count해서 정합성을 맞출수도 있음, table데이터가 많아지면 어떤방법이 좋을지 확인해봐야함
        if(oldType == ReactionType.LIKE) { postRepository.decrementLikeCount(postId); likeCount--; }
        else if(oldType == ReactionType.DISLIKE) { postRepository.decrementDislikeCount(postId); dislikeCount--; }

        if(newType == ReactionType.LIKE) { postRepository.incrementLikeCount(postId); likeCount++; }
        else if(newType == ReactionType.DISLIKE) { postRepository.incrementDislikeCount(postId); dislikeCount++; }

        return new PostReactionDto.Response(
                postId,
                newType,
                likeCount,
                dislikeCount
        );
    }

    @Transactional
    public void deleteReaction(Long postId, Long userId) {

        PostReaction reaction = postReactionRepository.findByPostIdAndUserId(postId, userId)
                        .orElse(null);

        if(reaction == null) return;

        postReactionRepository.delete(reaction);

        ReactionType type = reaction.getType();
        if(type == ReactionType.LIKE) postRepository.decrementLikeCount(postId);
        else if(type == ReactionType.DISLIKE) postRepository.decrementDislikeCount(postId);
    }
}
