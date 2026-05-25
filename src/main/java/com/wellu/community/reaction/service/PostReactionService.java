package com.wellu.community.reaction.service;

import com.wellu.community.post.repository.PostRepository;
import com.wellu.community.reaction.dto.PostReactionResponse;
import com.wellu.community.reaction.dto.PostReactionSummaryResponse;
import com.wellu.community.reaction.dto.ReactToPostRequest;
import com.wellu.community.reaction.dto.ReactionCountResponse;
import com.wellu.community.reaction.entity.PostReaction;
import com.wellu.community.reaction.mapper.PostReactionMapper;
import com.wellu.community.reaction.repository.PostReactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PostReactionService {

    private final PostRepository postRepository;
    private final PostReactionRepository postReactionRepository;
    private final PostReactionMapper postReactionMapper;

    public PostReactionService(
            PostRepository postRepository,
            PostReactionRepository postReactionRepository,
            PostReactionMapper postReactionMapper
    ) {
        this.postRepository = postRepository;
        this.postReactionRepository = postReactionRepository;
        this.postReactionMapper = postReactionMapper;
    }

    @Transactional
    public PostReactionResponse reactToPost(UUID postId, UUID userId, ReactToPostRequest request) {
        ensurePostExists(postId);

        PostReaction reaction = postReactionRepository.findByPostIdAndUserId(postId, userId)
                .orElseGet(() -> {
                    PostReaction newReaction = postReactionMapper.toEntity(request);
                    newReaction.setPostId(postId);
                    newReaction.setUserId(userId);
                    return newReaction;
                });

        reaction.setType(request.type());

        return postReactionMapper.toResponse(postReactionRepository.save(reaction));
    }

    @Transactional
    public void removeReaction(UUID postId, UUID userId) {
        ensurePostExists(postId);

        postReactionRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(postReactionRepository::delete);
    }

    @Transactional(readOnly = true)
    public PostReactionSummaryResponse getReactionSummary(UUID postId, UUID currentUserId) {
        ensurePostExists(postId);

        List<ReactionCountResponse> counts = postReactionRepository.countGroupedByType(postId)
                .stream()
                .map(projection -> new ReactionCountResponse(projection.getType(), projection.getCount()))
                .toList();

        var currentUserReaction = postReactionRepository.findByPostIdAndUserId(postId, currentUserId)
                .map(reaction -> reaction.getType())
                .orElse(null);

        return new PostReactionSummaryResponse(
                postId,
                postReactionRepository.countByPostId(postId),
                counts,
                currentUserReaction
        );
    }

    private void ensurePostExists(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found.");
        }
    }
}
