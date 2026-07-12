package com.railtrack.dashboard.service.impl;

import com.railtrack.ai.dto.AiHistoryResponse;
import com.railtrack.ai.entity.AiHistory;
import com.railtrack.ai.repository.AiHistoryRepository;
import com.railtrack.auth.entity.User;
import com.railtrack.auth.mapper.UserMapper;
import com.railtrack.auth.service.UserService;
import com.railtrack.dashboard.dto.DashboardResponse;
import com.railtrack.dashboard.service.DashboardSectionProvider;
import com.railtrack.dashboard.service.DashboardService;
import com.railtrack.pnr.dto.response.PnrHistoryResponse;
import com.railtrack.pnr.entity.PnrSearchHistory;
import com.railtrack.pnr.repository.PnrSearchHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service implementation for authenticated-user dashboard data.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger log =
            LoggerFactory.getLogger(DashboardServiceImpl.class);

    private static final int RECENT_HISTORY_LIMIT = 5;

    private final UserService userService;
    private final UserMapper userMapper;
    private final PnrSearchHistoryRepository pnrHistoryRepository;
    private final AiHistoryRepository aiHistoryRepository;
    private final List<DashboardSectionProvider> dashboardSectionProviders;

    public DashboardServiceImpl(UserService userService,
                                UserMapper userMapper,
                                PnrSearchHistoryRepository pnrHistoryRepository,
                                AiHistoryRepository aiHistoryRepository,
                                List<DashboardSectionProvider>
                                        dashboardSectionProviders) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.pnrHistoryRepository = pnrHistoryRepository;
        this.aiHistoryRepository = aiHistoryRepository;
        this.dashboardSectionProviders = dashboardSectionProviders;
    }

    /**
     * Returns the dashboard summary for the authenticated user.
     */
    @Override
    public DashboardResponse getDashboard() {
        User currentUser = userService.getAuthenticatedUser();

        List<PnrHistoryResponse> recentPnrSearches =
                pnrHistoryRepository
                        .findByUserOrderBySearchedAtDesc(currentUser)
                        .stream()
                        .limit(RECENT_HISTORY_LIMIT)
                        .map(this::toPnrHistoryResponse)
                        .toList();

        List<AiHistoryResponse> recentAiHistory = aiHistoryRepository
                .findByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .limit(RECENT_HISTORY_LIMIT)
                .map(this::toAiHistoryResponse)
                .toList();

        Map<String, Object> additionalSections = new LinkedHashMap<>();
        dashboardSectionProviders.forEach(provider -> additionalSections.put(
                provider.getSectionName(),
                provider.getSectionData(currentUser)
        ));

        log.info("Dashboard retrieved for user {}", currentUser.getEmail());

        return new DashboardResponse(
                userMapper.toResponse(currentUser),
                recentPnrSearches,
                recentAiHistory,
                additionalSections
        );
    }

    private PnrHistoryResponse toPnrHistoryResponse(
            PnrSearchHistory history) {

        return new PnrHistoryResponse(
                history.getId(),
                history.getPnrNumber(),
                history.getTrainNumber(),
                history.getTrainName(),
                history.getSourceStation(),
                history.getDestinationStation(),
                history.getJourneyClass(),
                history.getChartStatus(),
                history.getSearchedAt()
        );
    }

    private AiHistoryResponse toAiHistoryResponse(AiHistory history) {
        return new AiHistoryResponse(
                history.getId(),
                history.getPnrNumber(),
                history.getAiResponse(),
                history.getCreatedAt()
        );
    }
}
