package com.wellu.community.reaction.dto;

import com.wellu.community.reaction.entity.ReactionType;
import jakarta.validation.constraints.NotNull;

public record ReactToPostRequest(
        @NotNull(message = "Reaction type is required.")
        ReactionType type
) {
}
