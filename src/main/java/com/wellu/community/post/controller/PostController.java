package com.wellu.community.post.controller;

import com.wellu.community.post.dto.CreatePostRequest;
import com.wellu.community.post.dto.PostResponse;
import com.wellu.community.post.service.PostService;
import com.wellu.community.security.CommunityUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal CommunityUserPrincipal principal,
            @Valid @RequestBody CreatePostRequest request
    ) {
        PostResponse response = postService.createPost(principal.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByAuthor(@PathVariable UUID userId) {
        return ResponseEntity.ok(postService.getPostsByAuthor(userId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal CommunityUserPrincipal principal
    ) {
        postService.deletePost(postId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }
}
