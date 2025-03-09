package io.openliberty.guides.app.domains;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;

@ApplicationScoped
public interface SpeedingViolationCalculator {
    int determineSpeedingViolationInKmh(LocalDateTime entryTimestamp, LocalDateTime exitTimestamp);
    String getRoadId();
}