package com.wellu.community.reaction.mapper;

import com.wellu.community.reaction.dto.PostReactionResponse;
import com.wellu.community.reaction.dto.ReactToPostRequest;
import com.wellu.community.reaction.entity.PostReaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostReactionMapper {

    PostReactionResponse toResponse(PostReaction reaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PostReaction toEntity(ReactToPostRequest request);
}
