package com.wellu.community.follow.service;

import com.wellu.community.feed.service.FeedFollowReader;
import com.wellu.community.follow.dto.FollowResponse;
import com.wellu.community.follow.dto.FollowSummaryResponse;
import com.wellu.community.follow.dto.FollowUserResponse;
import com.wellu.community.follow.graph.FollowGraphRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class FollowService implements FeedFollowReader {

    private final FollowGraphRepository followGraphRepository;

    public FollowService(FollowGraphRepository followGraphRepository) {
        this.followGraphRepository = followGraphRepository;
    }

    @Transactional(transactionManager = "neo4jTransactionManager")
    public FollowResponse follow(UUID followerId, UUID followeeId) {
        validateTarget(followerId, followeeId);

        followGraphRepository.follow(followerId, followeeId);

        return new FollowResponse(followerId, followeeId, true);
    }

    @Transactional(transactionManager = "neo4jTransactionManager")
    public void unfollow(UUID followerId, UUID followeeId) {
        validateTarget(followerId, followeeId);
        followGraphRepository.unfollow(followerId, followeeId);
    }

    @Transactional(transactionManager = "neo4jTransactionManager", readOnly = true)
    public List<FollowUserResponse> getFollowers(UUID userId) {
        return followGraphRepository.findFollowers(userId)
                .stream()
                .map(projection -> new FollowUserResponse(projection.getUserId()))
                .toList();
    }

    @Transactional(transactionManager = "neo4jTransactionManager", readOnly = true)
    public List<FollowUserResponse> getFollowing(UUID userId) {
        return followGraphRepository.findFollowing(userId)
                .stream()
                .map(projection -> new FollowUserResponse(projection.getUserId()))
                .toList();
    }

    @Override
    @Transactional(transactionManager = "neo4jTransactionManager", readOnly = true)
    public List<UUID> getFollowingUserIds(UUID userId) {
        return followGraphRepository.findFollowing(userId)
                .stream()
                .map(projection -> projection.getUserId())
                .toList();
    }

    @Transactional(transactionManager = "neo4jTransactionManager", readOnly = true)
    public FollowSummaryResponse getFollowSummary(UUID userId) {
        return new FollowSummaryResponse(
                userId,
                followGraphRepository.countFollowers(userId),
                followGraphRepository.countFollowing(userId)
        );
    }

    private void validateTarget(UUID followerId, UUID followeeId) {
        if (followerId.equals(followeeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot follow yourself.");
        }
    }
}
