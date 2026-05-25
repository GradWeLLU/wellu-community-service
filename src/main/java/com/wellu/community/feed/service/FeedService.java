package com.wellu.community.feed.service;

import com.wellu.community.feed.dto.FeedPostResponse;
import com.wellu.community.post.entity.Post;
import com.wellu.community.post.mapper.PostMapper;
import com.wellu.community.post.repository.PostRepository;
import com.wellu.community.reaction.dto.ReactionCountResponse;
import com.wellu.community.reaction.entity.PostReaction;
import com.wellu.community.reaction.entity.ReactionType;
import com.wellu.community.reaction.repository.PostReactionCountProjection;
import com.wellu.community.reaction.repository.PostReactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FeedService {

    private final FeedFollowReader feedFollowReader;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostReactionRepository postReactionRepository;

    public FeedService(
            FeedFollowReader feedFollowReader,
            PostRepository postRepository,
            PostMapper postMapper,
            PostReactionRepository postReactionRepository
    ) {
        this.feedFollowReader = feedFollowReader;
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.postReactionRepository = postReactionRepository;
    }

    public List<FeedPostResponse> getPersonalizedFeed(UUID userId) {
        List<UUID> followedUserIds = feedFollowReader.getFollowingUserIds(userId);

        if (followedUserIds.isEmpty()) {
            return List.of();
        }

        List<Post> posts = postRepository.findAllByAuthorIdInOrderByCreatedAtDesc(followedUserIds);

        if (posts.isEmpty()) {
            return List.of();
        }

        List<UUID> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        Map<UUID, List<ReactionCountResponse>> reactionCountsByPostId =
                buildReactionCountsByPostId(postReactionRepository.countGroupedByPostIds(postIds));

        Map<UUID, ReactionType> currentUserReactionByPostId =
                buildCurrentUserReactionByPostId(postReactionRepository.findAllByPostIdInAndUserId(postIds, userId));

        return posts.stream()
                .map(post -> {
                    var postResponse = postMapper.toResponse(post);
                    List<ReactionCountResponse> reactionCounts = reactionCountsByPostId.getOrDefault(post.getId(), List.of());
                    long totalReactions = reactionCounts.stream()
                            .mapToLong(ReactionCountResponse::count)
                            .sum();

                    return new FeedPostResponse(
                            postResponse.id(),
                            postResponse.authorId(),
                            postResponse.content(),
                            postResponse.createdAt(),
                            postResponse.updatedAt(),
                            totalReactions,
                            reactionCounts,
                            currentUserReactionByPostId.get(post.getId())
                    );
                })
                .toList();
    }

    private Map<UUID, List<ReactionCountResponse>> buildReactionCountsByPostId(
            List<PostReactionCountProjection> projections
    ) {
        Map<UUID, List<ReactionCountResponse>> countsByPostId = new HashMap<>();

        for (PostReactionCountProjection projection : projections) {
            countsByPostId.computeIfAbsent(projection.getPostId(), ignored -> new ArrayList<>())
                    .add(new ReactionCountResponse(projection.getType(), projection.getCount()));
        }

        return countsByPostId;
    }

    private Map<UUID, ReactionType> buildCurrentUserReactionByPostId(List<PostReaction> reactions) {
        Map<UUID, ReactionType> currentUserReactionByPostId = new HashMap<>();

        for (PostReaction reaction : reactions) {
            currentUserReactionByPostId.put(reaction.getPostId(), reaction.getType());
        }

        return currentUserReactionByPostId;
    }
}
