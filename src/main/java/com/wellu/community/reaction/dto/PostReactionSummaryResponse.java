package com.wellu.community.reaction.dto;

import com.wellu.community.reaction.entity.ReactionType;

import java.util.List;
import java.util.UUID;

public record PostReactionSummaryResponse(
        UUID postId,
        long totalReactions,
        List<ReactionCountResponse> counts,
        ReactionType currentUserReaction
) {
}
