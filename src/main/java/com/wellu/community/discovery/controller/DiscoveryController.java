package com.wellu.community.discovery.controller;

import com.wellu.community.discovery.dto.SuggestedUserResponse;
import com.wellu.community.discovery.service.DiscoveryService;
import com.wellu.community.security.CommunityUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/discovery")
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    public DiscoveryController(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<SuggestedUserResponse>> getSuggestions(
            @AuthenticationPrincipal CommunityUserPrincipal principal,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(discoveryService.getSuggestions(principal.getUserId(), limit));
    }
}
