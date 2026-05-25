package com.wellu.community.follow.dto;

import java.util.UUID;

public record FollowResponse(
        UUID followerId,
        UUID followeeId,
        boolean following
) {
}
