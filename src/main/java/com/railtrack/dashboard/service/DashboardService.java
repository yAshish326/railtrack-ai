package com.railtrack.dashboard.service;

import com.railtrack.dashboard.dto.DashboardResponse;

/**
 * Global facade that provides dashboard data for the authenticated user.
 *
 * <p>The controller depends only on this service. Feature modules can add
 * optional dashboard data through {@link DashboardSectionProvider} without
 * requiring controller changes.</p>
 */
public interface DashboardService {

    /**
     * Returns the authenticated user's dashboard summary.
     */
    DashboardResponse getDashboard();
}
