package com.wellu.community.reaction.dto;

import com.wellu.community.reaction.entity.ReactionType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PostReactionResponse(
        UUID id,
        UUID postId,
        UUID userId,
        ReactionType type,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
