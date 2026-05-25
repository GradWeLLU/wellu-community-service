package com.wellu.community.feed.dto;

import com.wellu.community.reaction.dto.ReactionCountResponse;
import com.wellu.community.reaction.entity.ReactionType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record FeedPostResponse(
        UUID id,
        UUID authorId,
        String content,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long totalReactions,
        List<ReactionCountResponse> reactionCounts,
        ReactionType currentUserReaction
) {
}
