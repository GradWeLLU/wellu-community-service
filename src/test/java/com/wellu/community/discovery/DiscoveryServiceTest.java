package com.wellu.community.discovery;

import com.wellu.community.discovery.dto.SuggestedUserResponse;
import com.wellu.community.discovery.service.DiscoveryService;
import com.wellu.community.discovery.service.SuggestedUserProjection;
import com.wellu.community.follow.graph.FollowGraphRepository;
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
class DiscoveryServiceTest {

    @Mock private FollowGraphRepository followGraphRepository;

    @InjectMocks
    private DiscoveryService discoveryService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    // ─── getSuggestions ──────────────────────────────────────────────────────

    @Test
    void getSuggestions_validLimit_returnsSuggestedUsers() {
        UUID suggestedUserId = UUID.randomUUID();
        SuggestedUserProjection projection = mock(SuggestedUserProjection.class);
        when(projection.getUserId()).thenReturn(suggestedUserId);
        when(projection.getMutualCount()).thenReturn(3L);

        when(followGraphRepository.suggestUsers(userId, 5)).thenReturn(List.of(projection));

        List<SuggestedUserResponse> result = discoveryService.getSuggestions(userId, 5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(suggestedUserId);
        assertThat(result.get(0).mutualCount()).isEqualTo(3L);
    }

    @Test
    void getSuggestions_noSuggestions_returnsEmptyList() {
        when(followGraphRepository.suggestUsers(userId, 5)).thenReturn(List.of());

        List<SuggestedUserResponse> result = discoveryService.getSuggestions(userId, 5);

        assertThat(result).isEmpty();
    }

    @Test
    void getSuggestions_limitZero_throwsBadRequest() {
        assertThatThrownBy(() -> discoveryService.getSuggestions(userId, 0))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Limit must be between 1 and 50");

        verifyNoInteractions(followGraphRepository);
    }

    @Test
    void getSuggestions_limitAbove50_throwsBadRequest() {
        assertThatThrownBy(() -> discoveryService.getSuggestions(userId, 51))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Limit must be between 1 and 50");

        verifyNoInteractions(followGraphRepository);
    }

    @Test
    void getSuggestions_limitAtBoundary1_succeeds() {
        when(followGraphRepository.suggestUsers(userId, 1)).thenReturn(List.of());

        List<SuggestedUserResponse> result = discoveryService.getSuggestions(userId, 1);

        assertThat(result).isEmpty();
        verify(followGraphRepository).suggestUsers(userId, 1);
    }

    @Test
    void getSuggestions_limitAtBoundary50_succeeds() {
        when(followGraphRepository.suggestUsers(userId, 50)).thenReturn(List.of());

        List<SuggestedUserResponse> result = discoveryService.getSuggestions(userId, 50);

        assertThat(result).isEmpty();
        verify(followGraphRepository).suggestUsers(userId, 50);
    }
}