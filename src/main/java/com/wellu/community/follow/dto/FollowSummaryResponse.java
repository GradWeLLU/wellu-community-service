package com.wellu.community.follow.dto;

import java.util.UUID;

public record FollowSummaryResponse(
        UUID userId,
        long followersCount,
        long followingCount
) {
}
