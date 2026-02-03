package com.example.online.elasticsearch.helper;

import java.util.List;

public record BulkResult(List<String> successIds, List<String> failedIds) {
}
