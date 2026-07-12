package com.railtrack.dashboard.service;

import com.railtrack.dashboard.dto.DashboardResponse;

/**
 * Global facade that provides dashboard data for the authenticated user.
 *
 * <p>The controller depends only on this service, so new dashboard data can
 * be added without changing the controller contract.</p>
 */
public interface DashboardService {

    /**
     * Returns the authenticated user's dashboard summary.
     */
    DashboardResponse getDashboard();
}
