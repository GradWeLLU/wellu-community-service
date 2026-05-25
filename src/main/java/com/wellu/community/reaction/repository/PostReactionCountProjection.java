package com.wellu.community.reaction.repository;

import com.wellu.community.reaction.entity.ReactionType;

import java.util.UUID;

public interface PostReactionCountProjection {

    UUID getPostId();

    ReactionType getType();

    long getCount();
}
