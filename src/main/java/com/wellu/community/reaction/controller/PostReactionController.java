package com.wellu.community.reaction.controller;

import com.wellu.community.reaction.dto.PostReactionResponse;
import com.wellu.community.reaction.dto.PostReactionSummaryResponse;
import com.wellu.community.reaction.dto.ReactToPostRequest;
import com.wellu.community.reaction.service.PostReactionService;
import com.wellu.community.security.CommunityUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts/{postId}/reactions")
public class PostReactionController {

    private final PostReactionService postReactionService;

    public PostReactionController(PostReactionService postReactionService) {
        this.postReactionService = postReactionService;
    }

    @PutMapping
    public ResponseEntity<PostReactionResponse> reactToPost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal CommunityUserPrincipal principal,
            @Valid @RequestBody ReactToPostRequest request
    ) {
        return ResponseEntity.ok(
                postReactionService.reactToPost(postId, principal.getUserId(), request)
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> removeReaction(
            @PathVariable UUID postId,
            @AuthenticationPrincipal CommunityUserPrincipal principal
    ) {
        postReactionService.removeReaction(postId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<PostReactionSummaryResponse> getReactionSummary(
            @PathVariable UUID postId,
            @AuthenticationPrincipal CommunityUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                postReactionService.getReactionSummary(postId, principal.getUserId())
        );
    }
}
