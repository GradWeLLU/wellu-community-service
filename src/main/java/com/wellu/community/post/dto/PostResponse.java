package com.wellu.community.post.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PostResponse(
        UUID id,
        UUID authorId,
        String content,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
