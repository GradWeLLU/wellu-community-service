package com.wellu.community.reaction.repository;

import com.wellu.community.reaction.entity.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostReactionRepository extends JpaRepository<PostReaction, UUID> {

    Optional<PostReaction> findByPostIdAndUserId(UUID postId, UUID userId);

    List<PostReaction> findAllByPostIdInAndUserId(List<UUID> postIds, UUID userId);

    long countByPostId(UUID postId);

    @Query("""
            select reaction.type as type, count(reaction) as count
            from PostReaction reaction
            where reaction.postId = :postId
            group by reaction.type
            """)
    List<ReactionCountProjection> countGroupedByType(@Param("postId") UUID postId);

    @Query("""
            select reaction.postId as postId, reaction.type as type, count(reaction) as count
            from PostReaction reaction
            where reaction.postId in :postIds
            group by reaction.postId, reaction.type
            """)
    List<PostReactionCountProjection> countGroupedByPostIds(@Param("postIds") List<UUID> postIds);
}
