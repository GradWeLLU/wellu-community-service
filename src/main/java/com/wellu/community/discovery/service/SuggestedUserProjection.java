package com.wellu.community.discovery.service;

import java.util.UUID;

public interface SuggestedUserProjection {

    UUID getUserId();

    long getMutualCount();
}
