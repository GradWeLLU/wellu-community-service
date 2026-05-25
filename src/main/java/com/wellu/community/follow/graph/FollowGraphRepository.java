package com.wellu.community.follow.graph;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FollowGraphRepository extends Neo4jRepository<UserNode, UUID> {

    @Query("""
            MERGE (follower:User {id: $followerId})
            MERGE (followee:User {id: $followeeId})
            MERGE (follower)-[:FOLLOWS]->(followee)
            """)
    void follow(@Param("followerId") UUID followerId, @Param("followeeId") UUID followeeId);

    @Query("""
            MATCH (follower:User {id: $followerId})-[relationship:FOLLOWS]->(followee:User {id: $followeeId})
            DELETE relationship
            """)
    void unfollow(@Param("followerId") UUID followerId, @Param("followeeId") UUID followeeId);

    @Query("""
            MATCH (:User {id: $followerId})-[relationship:FOLLOWS]->(:User {id: $followeeId})
            RETURN COUNT(relationship) > 0
            """)
    boolean isFollowing(@Param("followerId") UUID followerId, @Param("followeeId") UUID followeeId);

    @Query("""
            MATCH (follower:User)-[:FOLLOWS]->(:User {id: $userId})
            RETURN follower.id AS userId
            ORDER BY follower.id
            """)
    List<FollowUserIdProjection> findFollowers(@Param("userId") UUID userId);

    @Query("""
            MATCH (:User {id: $userId})-[:FOLLOWS]->(followee:User)
            RETURN followee.id AS userId
            ORDER BY followee.id
            """)
    List<FollowUserIdProjection> findFollowing(@Param("userId") UUID userId);

    @Query("""
            MATCH (:User)-[relationship:FOLLOWS]->(:User {id: $userId})
            RETURN COUNT(relationship)
            """)
    long countFollowers(@Param("userId") UUID userId);

    @Query("""
            MATCH (:User {id: $userId})-[relationship:FOLLOWS]->(:User)
            RETURN COUNT(relationship)
            """)
    long countFollowing(@Param("userId") UUID userId);

    @Query("""
            MATCH (me:User {id: $userId})-[:FOLLOWS]->(mutual:User)-[:FOLLOWS]->(suggested:User)
            WHERE suggested.id <> $userId
              AND NOT (me)-[:FOLLOWS]->(suggested)
            RETURN suggested.id AS userId, COUNT(DISTINCT mutual) AS mutualCount
            ORDER BY mutualCount DESC, suggested.id
            LIMIT $limit
            """)
    List<com.wellu.community.discovery.service.SuggestedUserProjection> suggestUsers(
            @Param("userId") UUID userId,
            @Param("limit") int limit
    );
}
