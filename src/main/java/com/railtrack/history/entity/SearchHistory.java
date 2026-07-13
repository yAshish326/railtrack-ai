package com.railtrack.history.entity;

import com.railtrack.auth.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Normalized audit entry for an external search. It stores only user request
 * metadata, never the response returned by a railway provider.
 */
@Entity
@Table(name = "search_history")
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private SearchOperation operation;

    @Column(nullable = false, length = 64)
    private String primaryIdentifier;

    @Column(length = 16)
    private String secondaryIdentifier;

    @Column(nullable = false, updatable = false)
    private LocalDateTime searchedAt;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public SearchOperation getOperation() { return operation; }
    public void setOperation(SearchOperation operation) { this.operation = operation; }
    public String getPrimaryIdentifier() { return primaryIdentifier; }
    public void setPrimaryIdentifier(String primaryIdentifier) { this.primaryIdentifier = primaryIdentifier; }
    public String getSecondaryIdentifier() { return secondaryIdentifier; }
    public void setSecondaryIdentifier(String secondaryIdentifier) { this.secondaryIdentifier = secondaryIdentifier; }
    public LocalDateTime getSearchedAt() { return searchedAt; }
    public void setSearchedAt(LocalDateTime searchedAt) { this.searchedAt = searchedAt; }
}
