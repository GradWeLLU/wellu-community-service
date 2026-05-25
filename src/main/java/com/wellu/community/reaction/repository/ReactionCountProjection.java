package com.wellu.community.reaction.repository;

import com.wellu.community.reaction.entity.ReactionType;

public interface ReactionCountProjection {

    ReactionType getType();

    long getCount();
}
