package com.wellu.community.post.mapper;

import com.wellu.community.post.dto.CreatePostRequest;
import com.wellu.community.post.dto.PostResponse;
import com.wellu.community.post.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostResponse toResponse(Post post);

    List<PostResponse> toResponseList(List<Post> posts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Post toEntity(CreatePostRequest request);
}
