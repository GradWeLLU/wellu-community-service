package com.wellu.community.follow;

import com.wellu.community.follow.dto.FollowResponse;
import com.wellu.community.follow.dto.FollowSummaryResponse;
import com.wellu.community.follow.dto.FollowUserResponse;
import com.wellu.community.follow.graph.FollowGraphRepository;
import com.wellu.community.follow.graph.FollowUserIdProjection;
import com.wellu.community.follow.service.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock private FollowGraphRepository followGraphRepository;

    @InjectMocks
    private FollowService followService;

    private UUID followerId;
    private UUID followeeId;

    @BeforeEach
    void setUp() {
        followerId = UUID.randomUUID();
        followeeId = UUID.randomUUID();
    }

    // ─── follow ──────────────────────────────────────────────────────────────

    @Test
    void follow_validUsers_createsRelationshipAndReturnsResponse() {
        doNothing().when(followGraphRepository).follow(followerId, followeeId);

        FollowResponse result = followService.follow(followerId, followeeId);

        assertThat(result.followerId()).isEqualTo(followerId);
        assertThat(result.followeeId()).isEqualTo(followeeId);
        assertThat(result.following()).isTrue();
        verify(followGraphRepository).follow(followerId, followeeId);
    }

    @Test
    void follow_selfFollow_throwsBadRequest() {
        assertThatThrownBy(() -> followService.follow(followerId, followerId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("You cannot follow yourself");

        verify(followGraphRepository, never()).follow(any(), any());
    }

    // ─── unfollow ────────────────────────────────────────────────────────────

    @Test
    void unfollow_validUsers_removesRelationship() {
        doNothing().when(followGraphRepository).unfollow(followerId, followeeId);

        followService.unfollow(followerId, followeeId);

        verify(followGraphRepository).unfollow(followerId, followeeId);
    }

    @Test
    void unfollow_selfUnfollow_throwsBadRequest() {
        assertThatThrownBy(() -> followService.unfollow(followerId, followerId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("You cannot follow yourself");

        verify(followGraphRepository, never()).unfollow(any(), any());
    }

    // ─── getFollowers ────────────────────────────────────────────────────────

    @Test
    void getFollowers_returnsFollowerList() {
        FollowUserIdProjection projection = mock(FollowUserIdProjection.class);
        when(projection.getUserId()).thenReturn(followerId);
        when(followGraphRepository.findFollowers(followeeId)).thenReturn(List.of(projection));

        List<FollowUserResponse> result = followService.getFollowers(followeeId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(followerId);
    }

    @Test
    void getFollowers_noFollowers_returnsEmptyList() {
        when(followGraphRepository.findFollowers(followeeId)).thenReturn(List.of());

        List<FollowUserResponse> result = followService.getFollowers(followeeId);

        assertThat(result).isEmpty();
    }

    // ─── getFollowing ────────────────────────────────────────────────────────

    @Test
    void getFollowing_returnsFollowingList() {
        FollowUserIdProjection projection = mock(FollowUserIdProjection.class);
        when(projection.getUserId()).thenReturn(followeeId);
        when(followGraphRepository.findFollowing(followerId)).thenReturn(List.of(projection));

        List<FollowUserResponse> result = followService.getFollowing(followerId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(followeeId);
    }

    @Test
    void getFollowing_noFollowing_returnsEmptyList() {
        when(followGraphRepository.findFollowing(followerId)).thenReturn(List.of());

        List<FollowUserResponse> result = followService.getFollowing(followerId);

        assertThat(result).isEmpty();
    }

    // ─── getFollowingUserIds ─────────────────────────────────────────────────

    @Test
    void getFollowingUserIds_returnsMappedUUIDs() {
        FollowUserIdProjection projection = mock(FollowUserIdProjection.class);
        when(projection.getUserId()).thenReturn(followeeId);
        when(followGraphRepository.findFollowing(followerId)).thenReturn(List.of(projection));

        List<UUID> result = followService.getFollowingUserIds(followerId);

        assertThat(result).containsExactly(followeeId);
    }

    // ─── getFollowSummary ────────────────────────────────────────────────────

    @Test
    void getFollowSummary_returnsCorrectCounts() {
        when(followGraphRepository.countFollowers(followerId)).thenReturn(5L);
        when(followGraphRepository.countFollowing(followerId)).thenReturn(3L);

        FollowSummaryResponse result = followService.getFollowSummary(followerId);

        assertThat(result.userId()).isEqualTo(followerId);
        assertThat(result.followersCount()).isEqualTo(5L);
        assertThat(result.followingCount()).isEqualTo(3L);
    }

    @Test
    void getFollowSummary_zeroCounts_returnsZeros() {
        when(followGraphRepository.countFollowers(followerId)).thenReturn(0L);
        when(followGraphRepository.countFollowing(followerId)).thenReturn(0L);

        FollowSummaryResponse result = followService.getFollowSummary(followerId);

        assertThat(result.followersCount()).isZero();
        assertThat(result.followingCount()).isZero();
    }
}