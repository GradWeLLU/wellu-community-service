package com.wellu.community.feed.controller;

import com.wellu.community.feed.dto.FeedPostResponse;
import com.wellu.community.feed.service.FeedService;
import com.wellu.community.security.CommunityUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping
    public ResponseEntity<List<FeedPostResponse>> getFeed(
            @AuthenticationPrincipal CommunityUserPrincipal principal
    ) {
        return ResponseEntity.ok(feedService.getPersonalizedFeed(principal.getUserId()));
    }
}
