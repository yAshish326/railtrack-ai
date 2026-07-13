package com.railtrack.train.service;

import com.railtrack.train.dto.request.TrainSearchHistoryRequest;
import com.railtrack.train.dto.response.TrainSearchHistoryResponse;

import java.util.List;

/** Provides authenticated-user train-search history operations. */
public interface TrainSearchHistoryService {

    void saveSearch(TrainSearchHistoryRequest request);

    List<TrainSearchHistoryResponse> getHistory();

    void deleteHistory();

    void deleteHistoryById(Long historyId);

    long countSearches();
}
