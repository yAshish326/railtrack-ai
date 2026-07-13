package com.railtrack.dashboard.service.impl;

import com.railtrack.ai.entity.AiHistory;
import com.railtrack.ai.repository.AiHistoryRepository;
import com.railtrack.auth.entity.User;
import com.railtrack.auth.mapper.UserMapper;
import com.railtrack.auth.service.UserService;
import com.railtrack.dashboard.dto.DashboardAiHistoryResponse;
import com.railtrack.dashboard.dto.DashboardPnrResponse;
import com.railtrack.dashboard.dto.DashboardResponse;
import com.railtrack.dashboard.dto.DashboardStatsResponse;
import com.railtrack.dashboard.dto.DashboardTrainSearchResponse;
import com.railtrack.dashboard.service.DashboardService;
import com.railtrack.pnr.entity.PnrSearchHistory;
import com.railtrack.pnr.repository.PnrSearchHistoryRepository;
import com.railtrack.train.entity.TrainSearchHistory;
import com.railtrack.train.repository.TrainSearchHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Service implementation for authenticated-user dashboard data.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger log =
            LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final UserService userService;
    private final UserMapper userMapper;
    private final PnrSearchHistoryRepository pnrHistoryRepository;
    private final AiHistoryRepository aiHistoryRepository;
    private final TrainSearchHistoryRepository trainSearchHistoryRepository;

    public DashboardServiceImpl(UserService userService,
                                UserMapper userMapper,
                                PnrSearchHistoryRepository pnrHistoryRepository,
                                AiHistoryRepository aiHistoryRepository,
                                TrainSearchHistoryRepository trainSearchHistoryRepository) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.pnrHistoryRepository = pnrHistoryRepository;
        this.aiHistoryRepository = aiHistoryRepository;
        this.trainSearchHistoryRepository = trainSearchHistoryRepository;
    }

    /**
     * Returns the dashboard summary for the authenticated user.
     */
    @Override
    public DashboardResponse getDashboard() {
        User currentUser = Objects.requireNonNull(
                userService.getAuthenticatedUser(),
                "Authenticated user is required."
        );

        List<DashboardPnrResponse> recentPnrSearches =
                pnrHistoryRepository
                        .findTop5ByUserOrderBySearchedAtDesc(currentUser)
                        .stream()
                        .map(this::toDashboardPnrResponse)
                        .toList();

        List<DashboardTrainSearchResponse> recentTrainSearches =
                trainSearchHistoryRepository
                        .findTop5ByUserOrderBySearchedAtDesc(currentUser)
                        .stream()
                        .map(this::toDashboardTrainSearchResponse)
                        .toList();

        List<DashboardAiHistoryResponse> recentAiHistory = aiHistoryRepository
                .findTop5ByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(this::toDashboardAiHistoryResponse)
                .toList();

        DashboardStatsResponse stats = new DashboardStatsResponse(
                Math.toIntExact(trainSearchHistoryRepository.countByUser(currentUser)),
                Math.toIntExact(pnrHistoryRepository.countByUser(currentUser)),
                Math.toIntExact(aiHistoryRepository.countByUser(currentUser))
        );

        log.info("Dashboard retrieved for user {}", currentUser.getEmail());

        return new DashboardResponse(
                userMapper.toResponse(currentUser),
                stats,
                recentTrainSearches,
                recentPnrSearches,
                recentAiHistory
        );
    }

    private DashboardTrainSearchResponse toDashboardTrainSearchResponse(
            TrainSearchHistory history) {
        return new DashboardTrainSearchResponse(history.getId(),
                history.getFromStation(), history.getToStation(),
                history.getJourneyDate(), history.getTravelClass(),
                history.getQuota(), history.getSearchedAt());
    }

    private DashboardPnrResponse toDashboardPnrResponse(
            PnrSearchHistory history) {

        return new DashboardPnrResponse(
                history.getId(),
                history.getPnrNumber(),
                history.getTrainName(),
                history.getTrainNumber(),
                history.getSourceStation(),
                history.getDestinationStation(),
                history.getChartStatus(),
                history.getSearchedAt()
        );
    }

    private DashboardAiHistoryResponse toDashboardAiHistoryResponse(
            AiHistory history) {

        return new DashboardAiHistoryResponse(
                history.getId(),
                history.getPrompt() == null ? "" : history.getPrompt(),
                history.getCreatedAt()
        );
    }
}
