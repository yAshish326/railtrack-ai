package com.railtrack.dashboard.service;

import com.railtrack.auth.entity.User;

/**
 * Extension point for modules that contribute data to the user dashboard.
 *
 * <p>A future module can implement this interface as a Spring bean to expose
 * a DTO-backed section without changing the dashboard controller.</p>
 */
public interface DashboardSectionProvider {

    /**
     * Returns the unique JSON property name for this dashboard section.
     */
    String getSectionName();

    /**
     * Returns DTO-backed section data for the supplied authenticated user.
     */
    Object getSectionData(User user);
}
