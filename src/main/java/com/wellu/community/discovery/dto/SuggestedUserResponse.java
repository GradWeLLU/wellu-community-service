package com.wellu.community.discovery.dto;

import java.util.UUID;

public record SuggestedUserResponse(
        UUID userId,
        long mutualCount
) {
}
