package com.wellu.community.post.service;

import com.wellu.community.post.dto.CreatePostRequest;
import com.wellu.community.post.dto.PostResponse;
import com.wellu.community.post.entity.Post;
import com.wellu.community.post.mapper.PostMapper;
import com.wellu.community.post.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    @Transactional
    public PostResponse createPost(UUID authorId, CreatePostRequest request) {
        Post post = postMapper.toEntity(request);
        post.setAuthorId(authorId);
        post.setContent(post.getContent().trim());

        return postMapper.toResponse(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

        return postMapper.toResponse(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByAuthor(UUID authorId) {
        return postMapper.toResponseList(postRepository.findAllByAuthorIdOrderByCreatedAtDesc(authorId));
    }

    @Transactional
    public void deletePost(UUID postId, UUID authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

        if (!post.getAuthorId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own posts.");
        }

        postRepository.delete(post);
    }
}
