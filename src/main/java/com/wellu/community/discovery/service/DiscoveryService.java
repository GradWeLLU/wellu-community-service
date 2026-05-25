package com.wellu.community.discovery.service;

import com.wellu.community.discovery.dto.SuggestedUserResponse;
import com.wellu.community.follow.graph.FollowGraphRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class DiscoveryService {

    private final FollowGraphRepository followGraphRepository;

    public DiscoveryService(FollowGraphRepository followGraphRepository) {
        this.followGraphRepository = followGraphRepository;
    }

    @Transactional(transactionManager = "neo4jTransactionManager", readOnly = true)
    public List<SuggestedUserResponse> getSuggestions(UUID userId, int limit) {
        if (limit < 1 || limit > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit must be between 1 and 50.");
        }

        return followGraphRepository.suggestUsers(userId, limit)
                .stream()
                .map(projection -> new SuggestedUserResponse(
                        projection.getUserId(),
                        projection.getMutualCount()
                ))
                .toList();
    }
}
