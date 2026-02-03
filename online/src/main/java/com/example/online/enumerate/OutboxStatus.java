package com.example.online.enumerate;

public enum OutboxStatus {
    NEW,
    PROCESSING,
    RETRY,
    DONE,
    FAILED
}
