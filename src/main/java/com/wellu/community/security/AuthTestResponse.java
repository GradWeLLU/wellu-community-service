package com.wellu.community.security;

import java.util.UUID;

public record AuthTestResponse(
        UUID userId,
        String username,
        boolean authenticated
) {
}
