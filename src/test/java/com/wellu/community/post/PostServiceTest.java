package com.wellu.community.post;

import com.wellu.community.post.dto.CreatePostRequest;
import com.wellu.community.post.dto.PostResponse;
import com.wellu.community.post.entity.Post;
import com.wellu.community.post.mapper.PostMapper;
import com.wellu.community.post.repository.PostRepository;
import com.wellu.community.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private UUID authorId;
    private UUID postId;
    private Post post;
    private PostResponse postResponse;

    @BeforeEach
    void setUp() {
        authorId = UUID.randomUUID();
        postId = UUID.randomUUID();

        post = new Post();
        post.setId(postId);
        post.setAuthorId(authorId);
        post.setContent("My first workout post!");
        post.setCreatedAt(OffsetDateTime.now());
        post.setUpdatedAt(OffsetDateTime.now());

        postResponse = new PostResponse(postId, authorId, "My first workout post!", post.getCreatedAt(), post.getUpdatedAt());
    }

    // ─── createPost ──────────────────────────────────────────────────────────

    @Test
    void createPost_validRequest_returnsPostResponse() {
        CreatePostRequest request = new CreatePostRequest("My first workout post!");

        when(postMapper.toEntity(request)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        PostResponse result = postService.createPost(authorId, request);

        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("My first workout post!");
        assertThat(result.authorId()).isEqualTo(authorId);
        verify(postRepository).save(post);
    }

    @Test
    void createPost_contentIsTrimmed() {
        CreatePostRequest request = new CreatePostRequest("  My first workout post!  ");

        Post postWithWhitespace = new Post();
        postWithWhitespace.setId(postId);
        postWithWhitespace.setAuthorId(authorId);
        postWithWhitespace.setContent("  My first workout post!  ");

        when(postMapper.toEntity(request)).thenReturn(postWithWhitespace);
        when(postRepository.save(postWithWhitespace)).thenReturn(postWithWhitespace);
        when(postMapper.toResponse(postWithWhitespace)).thenReturn(postResponse);

        postService.createPost(authorId, request);

        assertThat(postWithWhitespace.getContent()).isEqualTo("My first workout post!");
    }

    // ─── getPostById ─────────────────────────────────────────────────────────

    @Test
    void getPostById_existingPost_returnsResponse() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        PostResponse result = postService.getPostById(postId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(postId);
    }

    @Test
    void getPostById_notFound_throwsNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPostById(postId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Post not found");
    }

    // ─── getPostsByAuthor ────────────────────────────────────────────────────

    @Test
    void getPostsByAuthor_returnsMappedList() {
        when(postRepository.findAllByAuthorIdOrderByCreatedAtDesc(authorId)).thenReturn(List.of(post));
        when(postMapper.toResponseList(List.of(post))).thenReturn(List.of(postResponse));

        List<PostResponse> result = postService.getPostsByAuthor(authorId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).authorId()).isEqualTo(authorId);
    }

    @Test
    void getPostsByAuthor_noPosts_returnsEmptyList() {
        when(postRepository.findAllByAuthorIdOrderByCreatedAtDesc(authorId)).thenReturn(List.of());
        when(postMapper.toResponseList(List.of())).thenReturn(List.of());

        List<PostResponse> result = postService.getPostsByAuthor(authorId);

        assertThat(result).isEmpty();
    }

    // ─── deletePost ──────────────────────────────────────────────────────────

    @Test
    void deletePost_ownerDeletes_succeeds() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId, authorId);

        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_notFound_throwsNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.deletePost(postId, authorId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Post not found");

        verify(postRepository, never()).delete(any());
    }

    @Test
    void deletePost_wrongAuthor_throwsForbidden() {
        UUID otherUserId = UUID.randomUUID();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(postId, otherUserId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("You can only delete your own posts");

        verify(postRepository, never()).delete(any());
    }
}