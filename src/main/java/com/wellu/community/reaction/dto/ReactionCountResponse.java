package com.wellu.community.reaction.dto;

import com.wellu.community.reaction.entity.ReactionType;

public record ReactionCountResponse(
        ReactionType type,
        long count
) {
}
