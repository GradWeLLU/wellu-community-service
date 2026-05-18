package com.wellu.community.post.repository;

import com.wellu.community.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByAuthorIdOrderByCreatedAtDesc(UUID authorId);

    Optional<Post> findByIdAndAuthorId(UUID id, UUID authorId);
}
