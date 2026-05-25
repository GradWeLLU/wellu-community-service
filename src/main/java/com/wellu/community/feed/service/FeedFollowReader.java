package com.wellu.community.feed.service;

import java.util.List;
import java.util.UUID;

public interface FeedFollowReader {

    List<UUID> getFollowingUserIds(UUID userId);
}
