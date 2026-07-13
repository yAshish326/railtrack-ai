package com.railtrack.train.exception;

/** Raised when a train-search history record is absent or belongs to another user. */
public class TrainSearchHistoryNotFoundException extends RuntimeException {

    public TrainSearchHistoryNotFoundException(String message) {
        super(message);
    }
}
