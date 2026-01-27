package com.example.online.event;

import java.util.List;

public record ModuleDeletedEvent(Long moduleId, List<Long> courseIds) {
}
