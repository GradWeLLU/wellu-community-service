package com.wellu.community.follow.controller;

import com.wellu.community.follow.dto.FollowResponse;
import com.wellu.community.follow.dto.FollowSummaryResponse;
import com.wellu.community.follow.dto.FollowUserResponse;
import com.wellu.community.follow.service.FollowService;
import com.wellu.community.security.CommunityUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{targetUserId}")
    public ResponseEntity<FollowResponse> follow(
            @PathVariable UUID targetUserId,
            @AuthenticationPrincipal CommunityUserPrincipal principal
    ) {
        FollowResponse response = followService.follow(principal.getUserId(), targetUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<Void> unfollow(
            @PathVariable UUID targetUserId,
            @AuthenticationPrincipal CommunityUserPrincipal principal
    ) {
        followService.unfollow(principal.getUserId(), targetUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<FollowUserResponse>> getFollowers(@PathVariable UUID userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<FollowUserResponse>> getFollowing(@PathVariable UUID userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<FollowSummaryResponse> getSummary(@PathVariable UUID userId) {
        return ResponseEntity.ok(followService.getFollowSummary(userId));
    }
}
