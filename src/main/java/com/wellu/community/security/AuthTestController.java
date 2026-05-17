package com.wellu.community.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthTestController {

    @GetMapping("/me")
    public ResponseEntity<AuthTestResponse> me(
            @AuthenticationPrincipal CommunityUserPrincipal principal
    ) {
        AuthTestResponse response = new AuthTestResponse(
                principal.getUserId(),
                principal.getUsername(),
                true
        );

        return ResponseEntity.ok(response);
    }
}
